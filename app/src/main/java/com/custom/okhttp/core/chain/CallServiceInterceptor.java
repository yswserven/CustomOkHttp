package com.custom.okhttp.core.chain;

import android.util.Log;

import com.custom.okhttp.core.HttpCodec;
import com.custom.okhttp.core.HttpConnection;
import com.custom.okhttp.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by: Ysw on 2020/3/6.
 */
public class CallServiceInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("Ysw", "intercept: 链接服务器拦截器...");
        HttpCodec httpCodec = chain.httpCodec;
        HttpConnection connection = chain.connection;
        InputStream is = connection.call(httpCodec);
        //HTTP/1.1 200 OK 空格隔开的响应状态
        String statusLine = httpCodec.readLine(is);
        Map<String, String> headers = httpCodec.readHeaders(is);
        //是否保持连接
        boolean isKeepAlive = false;
        if (headers.containsKey(HttpCodec.HEAD_CONNECTION)) {
            isKeepAlive = headers.get(HttpCodec.HEAD_CONNECTION)
                    .equalsIgnoreCase(HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        }
        //直接返回响应体 Response 中的 body
        int contentLength = -1;
        if (headers.containsKey(HttpCodec.HEAD_CONTENT_LENGTH)) {
            contentLength = Integer.valueOf(headers.get(HttpCodec.HEAD_CONTENT_LENGTH));
        }
        //分块编码数据
        boolean isChunked = false;
        if (headers.containsKey(HttpCodec.HEAD_TRANSFER_ENCODING)) {
            isChunked = headers.get(HttpCodec.HEAD_TRANSFER_ENCODING)
                    .equalsIgnoreCase(HttpCodec.HEAD_VALUE_CHUNKED);
        }
        String body = null;
        if (contentLength > 0) {
            byte[] bytes = httpCodec.readBytes(is, contentLength);
            body = new String(bytes);
        } else if (isChunked) {
            body = httpCodec.readChunked(is);
        }
        // 切分响应行的参数 获取响应码 200 表示响应成功
        String[] status = statusLine.split(" ");
        connection.updateLastUseTime();
        return new Response(Integer.valueOf(status[1]), contentLength,
                headers, body, isKeepAlive);
    }
}
