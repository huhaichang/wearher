package com.example.huhaichang.weather3.widget;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by huhaichang on 2019/8/16.
 */

public class OkhttpUtil {
    public static void sendHttpRequest(final String address, final okhttp3.Callback callback){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(address)
                        .build();
                client.newCall(request).enqueue(callback);
    }
}
