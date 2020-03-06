package com.custom.okhttp.core;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Ysw on 2020/3/5.
 * <p>
 * httpConnection 连接池
 */
public class ConnectionPool {

    /**
     * 垃圾回收线程
     * <p>
     * 设置为守护线程，当进程销毁后线程会销毁
     *
     * @author Ysw created at 2020/3/5 22:19
     */
    private static ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "HttpClient ConnectionPool");
            /* 设置为守护线程 @author Ysw created 2020/3/5 */
            thread.setDaemon(true);
            return thread;
        }
    };

    private static final Executor executor = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            threadFactory);

    private final Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long waitTimes = cleanup(System.currentTimeMillis());
                if (waitTimes == -1) {
                    return;
                }
                if (waitTimes > 0) {
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(waitTimes);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    /* 每个链接的最大存活时间 @author Ysw created 2020/3/5 */
    private final long keepAliveDuration;
    private final Deque<HttpConnection> connections = new ArrayDeque<>();
    private boolean cleanupRunning;

    ConnectionPool() {
        this(1, TimeUnit.MINUTES);
    }

    private ConnectionPool(long keepAliveDuration, TimeUnit timeUnit) {
        this.keepAliveDuration = timeUnit.toMillis(keepAliveDuration);
    }

    public void put(HttpConnection connection) {
        //执行检测清理
        if (!cleanupRunning) {
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        connections.add(connection);
    }

    public HttpConnection get(String host, int port) {
        Iterator<HttpConnection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            HttpConnection connection = iterator.next();
            if (connection.isSameAddress(host, port)) {
                iterator.remove();
                return connection;
            }
        }
        return null;
    }


    /**
     * 检查需要移除的连接返回下次检查时间
     *
     * @author Ysw created at 2020/3/5 22:26
     */
    private long cleanup(long now) {
        long longestIdleDuration = -1;
        synchronized (this) {
            for (Iterator<HttpConnection> iterator = connections.iterator(); iterator.hasNext(); ) {
                HttpConnection connection = iterator.next();
                //获得闲置时间 多长时间没使用这个了
                long idleDuration = now - connection.lastUseTime;
                //如果闲置时间超过允许
                if (idleDuration > keepAliveDuration) {
                    connection.closeQuietly();
                    iterator.remove();
                    Log.d("Ysw", "cleanup: 将连接移出连接池");
                    continue;
                }
                //获得最大闲置时间
                if (longestIdleDuration < idleDuration) {
                    longestIdleDuration = idleDuration;
                }
            }
            if (longestIdleDuration >= 0) {
                return keepAliveDuration - longestIdleDuration;
            } else {
                cleanupRunning = false;
                return longestIdleDuration;
            }
        }
    }
}
