package com.custom.okhttp.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class RequestBody {
    private final static String CONTENT_TYPE = "application/x-www-from-urlencoded";
    private final static String CHAREST = "utf-8";
    private Map<String, String> encodedBody = new HashMap<>();

    public String contentType() {
        return CONTENT_TYPE;
    }

    public long contentLength() {
        return body().getBytes().length;
    }

    private String body() {
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, String> entry : encodedBody.entrySet()) {
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
            encodedBody.put(URLEncoder.encode(name, CHAREST), URLEncoder.encode(value, CHAREST));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

}
