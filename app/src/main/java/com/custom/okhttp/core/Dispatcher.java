package com.custom.okhttp.core;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class Dispatcher {
    private int maxRequests;
    private int maxRequestsPreHost;
    private ExecutorService executorService;

    /* 等待执行的队列 @author Ysw created 2020/3/6 */
    private final Deque<Call.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /* 正在执行的队列 @author Ysw created 2020/3/6 */
    private final Deque<Call.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    Dispatcher() {
        this(64, 2);
    }

    private Dispatcher(int maxRequests, int maxRequestsPreHost) {
        this.maxRequests = maxRequests;
        this.maxRequestsPreHost = maxRequestsPreHost;
    }

    private synchronized ExecutorService executorService() {
        if (executorService == null) {
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "Http Client");
                    return thread;
                }
            };
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), threadFactory);
        }
        return executorService;
    }

    void enqueue(Call.AsyncCall call) {
        Log.d("Ysw", "enqueue: 同时有： " + runningAsyncCalls.size());
        Log.d("Ysw", "enqueue: Host 同时有： " + runningCallsForHost(call));
        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPreHost) {
            /* 提交执行 @author Ysw created 2020/3/6 */
            runningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            /* 加入等待执行队列 @author Ysw created 2020/3/6 */
            readyAsyncCalls.add(call);
        }
    }

    /**
     * 同一host 的 同时请求数
     *
     * @author Ysw created at 2020/3/6 10:23
     */
    private int runningCallsForHost(Call.AsyncCall call) {
        int result = 0;
        //如果执行这个请求，则相同的host数量是result
        for (Call.AsyncCall asyncCall : readyAsyncCalls) {
            if (asyncCall.host().equals(call.host())) {
                result++;
            }
        }
        return result;
    }


    /**
     * 请求结束 移出正在运行队列
     * 并判断是否执行等待队列中的请求
     *
     * @author Ysw created at 2020/3/6 10:23
     */
    void finished(Call.AsyncCall call) {
        synchronized (this) {
            runningAsyncCalls.remove(call);
            promoteCalls();
        }
    }

    /**
     * 判断是否执行等待队列中的请求
     *
     * @author Ysw created at 2020/3/6 10:23
     */
    private void promoteCalls() {
        //同时请求达到上限
        if (runningAsyncCalls.size() >= maxRequests) {
            return;
        }
        //没有等待执行请求
        if (readyAsyncCalls.isEmpty()) {
            return;
        }
        for (Iterator<Call.AsyncCall> iterator = readyAsyncCalls.iterator(); iterator.hasNext(); ) {
            Call.AsyncCall asyncCall = iterator.next();
            //同一host同时请求为达上限
            if (runningCallsForHost(asyncCall) < maxRequestsPreHost) {
                iterator.remove();
                runningAsyncCalls.add(asyncCall);
                executorService().execute(asyncCall);
            }
            //到达同时请求上限
            if (runningAsyncCalls.size() >= maxRequests) {
                return;
            }
        }
    }
}
