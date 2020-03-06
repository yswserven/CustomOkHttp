package com.custom.okhttp.core.chain;

import android.util.Log;

import com.custom.okhttp.core.HttpCodec;
import com.custom.okhttp.core.Request;
import com.custom.okhttp.core.Response;

import java.io.IOException;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/6.
 */
public class HeadersInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("Ysw", "intercept: Http 请求头拦截器...");
        Request request = chain.call.request();
        Map<String, String> headers = request.headers();
        headers.put(HttpCodec.HEAD_HOST, request.url().getHost());
        headers.put(HttpCodec.HEAD_CONNECTION, HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        if (null != request.body()) {
            String contentType = request.body().contentType();
            if (contentType != null) {
                headers.put(HttpCodec.HEAD_CONTENT_TYPE, contentType);
            }
            long contentLength = request.body().contentLength();
            if (contentLength != -1) {
                headers.put(HttpCodec.HEAD_CONTENT_LENGTH, Long.toString(contentLength));
            }
        }
        return chain.proceed();
    }
}
