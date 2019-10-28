package com.example.huhaichang.weather3.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by huhaichang on 2019/8/16.
 */

public class County extends LitePalSupport {
    private int id;
    private String countyName;
    private int cityId;       //所在市的id
    private String weatherId;  //所在县的天气数据码


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
