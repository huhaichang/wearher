package com.example.huhaichang.weather3.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huhaichang on 2019/8/17.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")

    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }

}
