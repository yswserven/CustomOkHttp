package com.custom.okhttp.core;

import com.custom.okhttp.core.chain.CallServiceInterceptor;
import com.custom.okhttp.core.chain.ConnectionInterceptor;
import com.custom.okhttp.core.chain.HeadersInterceptor;
import com.custom.okhttp.core.chain.Interceptor;
import com.custom.okhttp.core.chain.InterceptorChain;
import com.custom.okhttp.core.chain.RetryInterceptor;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class Call {
    private Request request;
    private HttpClient client;
    private boolean executed;
    private boolean canceled;

    Call(Request request, HttpClient client) {
        this.request = request;
        this.client = client;
    }

    public Request request() {
        return request;
    }

    public HttpClient client() {
        return client;
    }

    public Call enqueue(Callback callback) {
        //不能重复执行
        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already Executed");
            }
            executed = true;
        }
        client.dispatcher().enqueue(new AsyncCall(callback));
        return this;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public Response getResponse() throws IOException {
        ArrayList<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(new RetryInterceptor());
        interceptors.add(new HeadersInterceptor());
        interceptors.add(new ConnectionInterceptor());
        interceptors.add(new CallServiceInterceptor());
        InterceptorChain interceptorChain = new InterceptorChain(interceptors, 0, this, null);
        return interceptorChain.proceed();
    }

    final class AsyncCall implements Runnable {
        private final Callback callback;

        AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            /* 是否已经通知过 callback @author Ysw created 2020/3/6 */
            boolean signalledCallback = false;
            try {
                Response response = getResponse();
                if (canceled) {
                    signalledCallback = true;
                    callback.onFailure(Call.this, new IOException("Canceled"));
                } else {
                    signalledCallback = true;
                    callback.onResponse(Call.this, response);
                }
            } catch (IOException e) {
                if (!signalledCallback) {
                    callback.onFailure(Call.this, e);
                }
            } finally {
                client.dispatcher().finished(this);
            }
        }

       public String host() {
            return request.url().host;
        }
    }
}
