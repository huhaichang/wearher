package com.example.huhaichang.weather3.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by huhaichang on 2019/8/16.
 */

public class City extends LitePalSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;  //所在省的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
