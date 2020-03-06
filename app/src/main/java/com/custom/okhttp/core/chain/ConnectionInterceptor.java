package com.custom.okhttp.core.chain;

import android.util.Log;

import com.custom.okhttp.core.HttpClient;
import com.custom.okhttp.core.HttpConnection;
import com.custom.okhttp.core.Request;
import com.custom.okhttp.core.Response;

import java.io.IOException;

/**
 * Created by: Ysw on 2020/3/6.
 */
public class ConnectionInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("Ysw", "intercept: 链接拦截器...");
        Request request = chain.call.request();
        HttpClient client = chain.call.client();
        String host = request.url().getHost();
        int port = request.url().getPort();
        HttpConnection connection = client.connectionPool().get(host, port);
        if (null == connection) {
            connection = new HttpConnection();
        } else {
            Log.d("Ysw", "intercept: 使用的是连接池里面的 HttpConnection...");
        }
        try {
            connection.setRequest(request);
            Response response = chain.proceed(connection);
            if (response.isKeepAlive()) {
                client.connectionPool().put(connection);
            }
            return response;
        } catch (IOException e) {
            throw e;
        }
    }
}
