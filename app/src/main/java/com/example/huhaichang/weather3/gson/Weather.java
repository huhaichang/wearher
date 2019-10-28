package com.example.huhaichang.weather3.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by huhaichang on 2019/8/17.
 */

public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;


}
