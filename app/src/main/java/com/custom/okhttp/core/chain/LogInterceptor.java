package com.custom.okhttp.core.chain;

import android.util.Log;

import com.custom.okhttp.BuildConfig;
import com.custom.okhttp.core.Request;
import com.custom.okhttp.core.RequestBody;
import com.custom.okhttp.core.Response;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by: Ysw on 2020/3/6.
 * <p>
 * 请求日志拦截器,通过此拦截器实现请求和响应的日志打印
 */
public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Request request = chain.call.request();
        RequestBody body = request.body();
        if (BuildConfig.LOG_DEBUG && body != null) {
            Log.d("Ysw", "intercept: requestBody = " + new Gson().toJson(body.encodedBodys));
        }
        Response response = chain.proceed();
        String responseBody = response.getBody();
        if (BuildConfig.LOG_DEBUG && responseBody != null) {
            Log.d("Ysw", "intercept: responseBody = " + responseBody);
        }
        return response;
    }
}
