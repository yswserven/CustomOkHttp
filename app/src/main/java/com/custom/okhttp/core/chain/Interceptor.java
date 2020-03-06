package com.custom.okhttp.core.chain;

import com.custom.okhttp.core.Response;

import java.io.IOException;

/**
 * Created by: Ysw on 2020/3/5.
 */
public interface Interceptor {
    Response intercept(InterceptorChain chain) throws IOException;
}
