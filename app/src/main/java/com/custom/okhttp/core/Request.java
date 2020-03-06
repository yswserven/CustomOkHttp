package com.custom.okhttp.core;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class Request {
    private Map<String, String> headers;
    private String method;
    private HttpUrl url;
    private RequestBody body;

    private Request(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers;
        this.method = builder.method;
        this.body = builder.body;
    }

    String method() {
        return method;
    }

    public HttpUrl url() {
        return url;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public RequestBody body() {
        return body;
    }

    public final static class Builder {
        HttpUrl url;
        Map<String, String> headers = new HashMap<>();
        String method;
        RequestBody body;

        public Builder url(String url) {
            try {
                this.url = new HttpUrl(url);
                return this;
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Failed Http Url", e);
            }
        }

        public Builder addHearder(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder removeHearder(String name) {
            headers.remove(name);
            return this;
        }

        public Builder get() {
            method = "GET";
            return this;
        }

        public Builder post(RequestBody body) {
            this.body = body;
            method = "POST";
            return this;
        }

        public Request build() {
            if (url == null) {
                throw new IllegalStateException("请求的 url 地址为 null");
            }
            if (TextUtils.isEmpty(method)) {
                method = "GET";
            }
            return new Request(this);
        }
    }
}
