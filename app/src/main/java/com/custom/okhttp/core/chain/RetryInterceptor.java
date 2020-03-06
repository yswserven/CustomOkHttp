package com.custom.okhttp.core.chain;

import android.util.Log;

import com.custom.okhttp.core.Call;
import com.custom.okhttp.core.Response;

import java.io.IOException;

/**
 * Created by: Ysw on 2020/3/6.
 * <p>
 * 重试拦截器
 */
public class RetryInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("Ysw", "intercept: 重试拦截器...");
        Call call = chain.call;
        IOException exception = null;
        for (int i = 0; i < call.client().retrys(); i++) {
            if (call.isCanceled()) {
                throw new IOException("Canceled");
            }
            try {
                return chain.proceed();
            } catch (IOException e) {
                exception = e;
            }
        }
        throw exception;
    }
}
