package com.example.huhaichang.weather3.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huhaichang on 2019/8/17.
 */

public class Basic {
   @SerializedName("city")
   public String cityName;

    @SerializedName("id")

    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

}
