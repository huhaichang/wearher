package com.example.huhaichang.weather3.gpsdb;

import org.litepal.crud.LitePalSupport;

/**
 * Created by huhaichang on 2019/8/16.
 */

public class City2 extends LitePalSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private String provinceId;  //所在省的id

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

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }
}
