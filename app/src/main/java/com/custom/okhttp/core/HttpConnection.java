package com.custom.okhttp.core;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by: Ysw on 2020/3/5.
 * <p>
 * Socket 连接
 */
public class HttpConnection {
    private final String TAG = this.getClass().getSimpleName();
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private Request request;
    private static final String HTTPS = "https";
    long lastUseTime;

    public void setRequest(Request request) {
        this.request = request;
    }

    boolean isSameAddress(String host, int port) {
        if (null == socket) {
            return false;
        }
        return TextUtils.equals(socket.getInetAddress().getHostName(), host)
                && port == socket.getPort();
    }

    private void createSocket() throws IOException {
        if (null == socket || socket.isClosed()) {
            HttpUrl url = request.url();
            if (url.protocol.equalsIgnoreCase(HTTPS)) {
                socket = SSLSocketFactory.getDefault().createSocket();
            } else {
                socket = new Socket();
            }
            socket.connect(new InetSocketAddress(url.host, url.port));
            os = socket.getOutputStream();
            is = socket.getInputStream();
        }
    }

    public InputStream call(HttpCodec httpCodec) throws IOException {
        try {
            createSocket();
            httpCodec.writeRequest(os, request);
            return is;
        } catch (IOException e) {
            closeQuietly();
            throw new IOException(e);
        }
    }

    void closeQuietly() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateLastUseTime() {
        //更新最后使用的时间
        lastUseTime = System.currentTimeMillis();
    }
}
