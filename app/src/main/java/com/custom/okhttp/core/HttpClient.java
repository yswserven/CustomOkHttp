package com.custom.okhttp.core;

import com.custom.okhttp.core.chain.Interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class HttpClient {
    private Dispatcher dispatcher;
    private ConnectionPool connectionPool;
    private int retrys;
    private List<Interceptor> interceptors;

    private HttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.connectionPool = builder.connectionPool;
        this.retrys = builder.retrys;
        this.interceptors = builder.interceptors;

    }

    public Call newCall(Request request) {
        return new Call(request, this);
    }

    public int retrys() {
        return retrys;
    }

    Dispatcher dispatcher() {
        return dispatcher;
    }

    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    List<Interceptor> interceptors() {
        return interceptors;
    }

    public static final class Builder {
        Dispatcher dispatcher;
        ConnectionPool connectionPool;
        int retrys = 3;
        List<Interceptor> interceptors = new ArrayList<>();

        public Builder dispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder connectionPool(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
            return this;
        }

        public Builder retrys(int retrys) {
            this.retrys = retrys;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public HttpClient build() {
            if (null == dispatcher) {
                dispatcher = new Dispatcher();
            }
            if (null == connectionPool) {
                connectionPool = new ConnectionPool();
            }
            return new HttpClient(this);
        }
    }
}
