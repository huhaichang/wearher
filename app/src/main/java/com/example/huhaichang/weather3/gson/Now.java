package com.example.huhaichang.weather3.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huhaichang on 2019/8/17.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }

}
