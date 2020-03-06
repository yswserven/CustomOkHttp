package com.custom.okhttp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/5.
 */
public class HttpCodec {
    private final String TAG = this.getClass().getSimpleName();
    private static final String CRLF = "\r\n";
    private static final int CR = 13;
    private static final int LF = 10;
    private static final String SPACE = " ";
    private static final String VERSION = "HTTP/:1.1";
    private static final String COLON = ":";

    public static final String HEAD_HOST = "Host";
    public static final String HEAD_CONNECTION = "Connection";
    public static final String HEAD_CONTENT_TYPE = "Content-Type";
    public static final String HEAD_CONTENT_LENGTH = "Content-Length";
    public static final String HEAD_TRANSFER_ENCODING = "Transfer-Encoding";

    public static final String HEAD_VALUE_KEEP_ALIVE = "Keep-Alive";
    public static final String HEAD_VALUE_CHUNKED = "chunked";

    private ByteBuffer byteBuffer;

    public HttpCodec() {
        byteBuffer = ByteBuffer.allocate(10 * 1024);
    }

    void writeRequest(OutputStream os, Request request) throws IOException {
        StringBuilder protocol = new StringBuilder();

        /* 拼接请求行 @author Ysw created 2020/3/5 */
        protocol.append(request.method());
        protocol.append(SPACE);
        protocol.append(request.url().file);
        protocol.append(SPACE);
        protocol.append(VERSION);
        protocol.append(CRLF);

        /* 拼接请求头 @author Ysw created 2020/3/5 */
        Map<String, String> headers = request.headers();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            protocol.append(entry.getKey());
            protocol.append(COLON);
            protocol.append(SPACE);
            protocol.append(entry.getValue());
            protocol.append(CRLF);
        }
        protocol.append(CRLF);

        /* 拼接请求体，如果存在请求体 @author Ysw created 2020/3/5 */
        RequestBody body = request.body();
        if (body != null) {
            protocol.append(body);
        }

        /* 通过 socket 获得的 os 写出请求 @author Ysw created 2020/3/5 */
        os.write(protocol.toString().getBytes());
        os.flush();
    }


    /**
     * 读取响应的请求头
     *
     * @author Ysw created at 2020/3/5 22:03
     */
    public Map<String, String> readHeaders(InputStream is) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String line = readLine(is);
            /* 读取到空行 则下面的为body @author Ysw created 2020/3/5 */
            if (isEmptyLine(line)) {
                break;
            }
            int index = line.indexOf(":");
            if (index > 0) {
                String name = line.substring(0, index);
                // ": "移动两位到 总长度减去两个("\r\n")
                String value = line.substring(index + 2, line.length() - 2);
                headers.put(name, value);
            }
        }
        return headers;
    }


    private boolean isEmptyLine(String line) {
        return line.equals("\r\n");
    }


    /**
     * 一行一行读取 Response 里面的内容
     *
     * @author Ysw created at 2020/3/5 22:02
     */
    public String readLine(InputStream is) throws IOException {
        try {
            byte b;
            boolean isMaybeEndOfLine = false;
            byteBuffer.clear();
            byteBuffer.mark();
            while ((b = (byte) is.read()) != -1) {
                byteBuffer.put(b);
                // 读取到/r则记录，判断下一个字节是否为/n
                if (b == CR) {
                    isMaybeEndOfLine = true;
                } else if (isMaybeEndOfLine) {
                    //上一个字节是/r 并且本次读取到/n
                    if (b == LF) {
                        byte[] lineBytes = new byte[byteBuffer.position()];
                        byteBuffer.reset();
                        byteBuffer.get(lineBytes);
                        byteBuffer.clear();
                        byteBuffer.mark();
                        return new String(lineBytes);
                    }
                    isMaybeEndOfLine = false;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        throw new IOException("Response Read Line Error 读取 Response 的数据时错误");
    }


    public byte[] readBytes(InputStream is, int len) throws IOException {
        byte[] bytes = new byte[len];
        int readNum = 0;
        while (true) {
            readNum += is.read(bytes, readNum, len - readNum);
            if (readNum == len) {
                return bytes;
            }
        }
    }


    public String readChunked(InputStream is) throws IOException {
        int len = -1;
        boolean isEmptyData = false;
        StringBuilder chunked = new StringBuilder();
        while (true) {
            //解析下一个chunk长度
            if (len < 0) {
                String line = readLine(is);
                line = line.substring(0, line.length() - 2);
                len = Integer.valueOf(line, 16);
                //chunk编码的数据最后一段为 0\r\n\r\n
                isEmptyData = len == 0;
            } else {
                //块长度不包括\r\n  所以+2将 \r\n 读走
                byte[] bytes = readBytes(is, len + 2);
                chunked.append(new String(bytes));
                len = -1;
                if (isEmptyData) {
                    return chunked.toString();
                }
            }
        }
    }
}
