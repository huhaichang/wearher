package com.example.huhaichang.weather3;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huhaichang.weather3.gson.Forecast;
import com.example.huhaichang.weather3.gson.Weather;
import com.example.huhaichang.weather3.widget.OkhttpUtil;
import com.example.huhaichang.weather3.widget.ToastUtil;
import com.example.huhaichang.weather3.widget.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by huhaichang on 2019/9/24.
 */

@SuppressLint("ValidFragment")
public class CityFragment extends Fragment {
    private TextView mTVUpdateTime;
    private String cityId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView mTVNowTemperature;
    private TextView mTVWeatherInfo;
    private LinearLayout mLLforecast;
    private TextView mTVAQI;
    private TextView mTVPM25;
    private TextView mTVComfort;
    private TextView mTVCarWash;
    private TextView mTVSport;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private ScrollView msvSync;

  @SuppressLint("ValidFragment")
  public CityFragment(String cityId){
        this.cityId = cityId;
  }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_city,container,false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            mTVUpdateTime = view.findViewById(R.id.tv_updateTime);
         mTVNowTemperature=view.findViewById(R.id.tv_nowTemperature);
         mTVWeatherInfo=view.findViewById(R.id.tv_weather_info);
         mLLforecast=view.findViewById(R.id.ll_forecast);
         mTVAQI=view.findViewById(R.id.tv_aqi);
         mTVPM25=view.findViewById(R.id.tv_pm25);
         mTVComfort=view.findViewById(R.id.tv_comfort);
         mTVCarWash=view.findViewById(R.id.tv_car_wash);
         mTVSport=view.findViewById(R.id.tv_sport);
         msvSync = view.findViewById(R.id.sv_sync);
         mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mSharedPreferences.edit();

         swipeRefreshLayout = view.findViewById(R.id.SRL_1);
         /**先显示旧数据*/
        String weatherString = mSharedPreferences.getString("weatherinfo",null);
        if(weatherString!=null){//先显示就数据 在更新数据  毕竟更新数据要时间 更新前没数据界面难看
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
            requestWeather(cityId);//就不弄下拉刷新了

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestWeather(cityId);
                    //刷新
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


    }
    public void requestWeather(final String weatherId){
        String url ="https://free-api.heweather.com/v5/weather?city="+weatherId+"&key=32d1c829ed7d483086f4f5b4d5947cef";
        OkhttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showMsg(getContext(),"获取天气信息失败");
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
               final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果可以解析成功的话
                        if (weather !=null && "ok".equals(weather.status)){
                            //设置ui
                            showWeatherInfo(weather);
                            cityId = weather.basic.weatherId;
                            mEditor.putString("weatherinfo",responseText);
                            mEditor.apply();
                        }else {
                            ToastUtil.showMsg(getContext(),"获取天气信息失败1");
                        }
                        //请求完了关闭
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                loadBingPic();
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        //今日状态
        mTVNowTemperature.setText(weather.now.temperature+"°C");
        mTVWeatherInfo.setText(weather.now.more.info);
        mTVUpdateTime.setText(" "+weather.basic.update.updateTime.split(" ")[1]+" 发布");
        //设置预报
        mLLforecast.removeAllViews();
        //列表里的每存在一个对象就把对象的值弄到forecast_item里在添加到LinerLayout里面去
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,mLLforecast,false);
            TextView dateText = view.findViewById(R.id.tv_date);
            TextView infoText = view.findViewById(R.id.tv_info);
            TextView maxText = view.findViewById(R.id.tv_max);
            TextView minText = view.findViewById(R.id.tv_min);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            mLLforecast.addView(view);//添加到LinerLayout里面去
        }
        //设置sqi指数
        if(weather.aqi!=null){
            mTVAQI.setText(weather.aqi.city.aqi);
            mTVPM25.setText(weather.aqi.city.pm25);
        }
        //建议
        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议: " + weather.suggestion.sport.info;
        mTVComfort.setText(comfort);
        mTVCarWash.setText(carWash);
        mTVSport.setText(sport);
    }
    private void loadBingPic(){
        String photoUrl ="http://guolin.tech/api/bing_pic";
        OkhttpUtil.sendHttpRequest(photoUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String photoAdress = response.body().string();
                //String photoAdress ="http://cn.bing.com/th?id=OHR.KelpKeepers_ROW3286873611_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
                //存入sharedperference数据
                mEditor.putString("bing_pic",photoAdress);
                mEditor.apply();
            }
        });
    }
}
