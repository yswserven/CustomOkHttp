package com.custom.okhttp.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class RequestBody {
    private final static String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final static String CHARSET = "utf-8";
    private Map<String, String> encodedBodys = new HashMap<>();

    public String contentType() {
        return CONTENT_TYPE;
    }

    public long contentLength() {
        return body().getBytes().length;
    }

    String body() {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> entry : encodedBodys.entrySet()) {
            buffer.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        if (buffer.length() != 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }

    public RequestBody add(String name, String value) {
        try {
            encodedBodys.put(URLEncoder.encode(name, CHARSET), URLEncoder.encode(value, CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

}
