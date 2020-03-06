package com.custom.okhttp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.custom.okhttp.R;
import com.custom.okhttp.core.Call;
import com.custom.okhttp.core.Callback;
import com.custom.okhttp.core.HttpClient;
import com.custom.okhttp.core.Request;
import com.custom.okhttp.core.RequestBody;
import com.custom.okhttp.core.Response;
import com.custom.okhttp.core.chain.LogInterceptor;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private HttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        httpClient = new HttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .retrys(3)
                .build();
    }

    public void get(View view) {
        Request request = new Request.Builder()
                .url("http://www.kuaidi100.com/query?type=yuantong&postid=222222222")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, Throwable throwable) {
                Log.d("Ysw", "onFailure: 请求失败");
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Ysw", "onResponse: 请求成功");
            }
        });

    }

    public void post(View view) {
        RequestBody requestBody = new RequestBody()
                .add("city", "长沙")
                .add("key", "13cb58f5884f9749287abbead9c658f2");
        Request request = new Request.Builder()
                .url("http://restapi.amap.com/v3/weather/weatherInfo")
                .post(requestBody)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, Throwable throwable) {
                Log.d("Ysw", "onFailure: 请求失败");
                throwable.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Ysw", "onResponse: 请求成功");
            }
        });
    }
}
