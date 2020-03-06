package com.custom.okhttp.core;

/**
 * Created by: Ysw on 2020/3/5.
 */
public interface Callback {
    void onFailure(Call call, Throwable throwable);

    void onResponse(Call call, Response response);
}
