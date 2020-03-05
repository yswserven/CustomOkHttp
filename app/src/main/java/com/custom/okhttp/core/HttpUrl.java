package com.custom.okhttp.core;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class HttpUrl {
    String protocol;
    String host;
    String file;
    int port;

    public HttpUrl(String url) throws MalformedURLException {
        URL url1 = new URL(url);
        host = url1.getHost();
        file = url1.getFile();
        file = TextUtils.isEmpty(file) ? "/" : file;
        protocol = url1.getProtocol();
        port = url1.getPort();
        port = port == -1 ? url1.getDefaultPort() : port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
