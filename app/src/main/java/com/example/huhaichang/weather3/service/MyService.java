package com.example.huhaichang.weather3.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.huhaichang.weather3.gson.Weather;
import com.example.huhaichang.weather3.widget.OkhttpUtil;
import com.example.huhaichang.weather3.widget.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyService extends Service {
    private AlarmManager manager;
    private PendingIntent pi;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updatePhoto();
        Log.i("1","进入服务");
        //服务的作用就是把最新的数据放入APP缓存中（但是我们读取的是上一次的缓存数据（过时的）） （所以没什么用  还不然直接刷新最新数据）
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long a = SystemClock.elapsedRealtime()+5*60*1000; //5分钟刷新一次  差不多更新一次缓存
        Intent intent1 = new Intent(MyService.this,MyService.class); //自跳转
        pi =PendingIntent.getService(MyService.this,0,intent1,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,a,pi);  //对时间要求不那么精准
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateWeather(){
        //直接重新发送请求 不能去调用activity的方法
        //如果有缓存就解析缓存获取cityId 在去更新缓存
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyService.this);
        String abc = sharedPreferences.getString("weather",null);
        if(abc!=null){
            //先获取weather对象的id
            final Weather weather = Utility.handleWeatherResponse(abc);
            final String weatherId = weather.basic.weatherId;
            String url = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=32d1c829ed7d483086f4f5b4d5947cef";
            OkhttpUtil.sendHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String urlString = response.body().string();  //最新网站源数据
                    //如果可以解析成功 就存入数据库
                    Weather weather1 = Utility.handleWeatherResponse(urlString);
                    if(weather!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(MyService.this).edit();
                        mEditor.putString("weather",urlString);
                        mEditor.apply();
                    }
                }
            });
        }

    }
    private void updatePhoto(){
        String photoUrl ="http://guolin.tech/api/bing_pic";
        OkhttpUtil.sendHttpRequest(photoUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String photoAdress = response.body().string();
                //存入sharedperference数据
              SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(MyService.this).edit();
                mEditor.putString("bing_pic",photoAdress);
                mEditor.apply();
                //存入就行 每次打开APP都Activity会先执行读取数据加载图片
            }
        });
    }
}
