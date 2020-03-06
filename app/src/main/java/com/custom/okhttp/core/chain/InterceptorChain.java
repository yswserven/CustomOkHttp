package com.custom.okhttp.core.chain;

import com.custom.okhttp.core.Call;
import com.custom.okhttp.core.HttpCodec;
import com.custom.okhttp.core.HttpConnection;
import com.custom.okhttp.core.Response;

import java.io.IOException;
import java.util.List;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class InterceptorChain {
    private final List<Interceptor> interceptors;
    private final int index;
    public final Call call;
    final HttpConnection connection;
    final HttpCodec httpCodec = new HttpCodec();

    public InterceptorChain(List<Interceptor> interceptors, int index,
                            Call call, HttpConnection connection) {
        this.interceptors = interceptors;
        this.index = index;
        this.call = call;
        this.connection = connection;
    }

    public Response proceed() throws IOException {
        return proceed(connection);
    }

    Response proceed(HttpConnection connection) throws IOException {
        Interceptor interceptor = interceptors.get(index);
        InterceptorChain chain = new InterceptorChain(interceptors,
                index + 1, call, connection);
        return interceptor.intercept(chain);
    }
}
