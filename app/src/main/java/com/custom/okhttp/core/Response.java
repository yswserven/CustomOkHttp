package com.custom.okhttp.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class Response {
    private int code;
    private int contentLength = -1;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    boolean isKeepAlive;

    public Response() {
    }

    public Response(int code, int contentLength, Map<String, String> headers, String body,
                    boolean isKeepAlive) {
        this.code = code;
        this.contentLength = contentLength;
        this.headers = headers;
        this.body = body;
        this.isKeepAlive = isKeepAlive;
    }

    public int getCode() {
        return code;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", contentLength=" + contentLength +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", isKeepAlive=" + isKeepAlive +
                '}';
    }
}
