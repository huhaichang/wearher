package com.example.huhaichang.weather3.gson;

/**
 * Created by huhaichang on 2019/8/19.
 */

public class Forexampel {
   /*化简的源码返回的数据
          {"HeWeather5":[
            {"status":"ok",
                    "basic":{"city":"永定a","id":"CN101251105","update":{"loc":"2016-08-08 21:58"}},
                "aqi":{"city":{"aqi":"44","pm25":"13"}},
                "now":{"tmp":"29","cond":{"txt":"晴"}},
                "daily_forecast":[{"date":"2016-08-08","cond":{"txt_d":"3"},"tmp":{"max":"34","min":"27"}}],
                "suggestion":{"comf":{"txt":"1"},"cw":{"txt":"2"},"sport":{"txt":"3"}}
            }]}
        jsonObject解析后的数据


            {"status":"ok",
            "basic":{"city":"永定a","id":"CN101251105","update":{"loc":"2016-08-08 21:58"}},
            "aqi":{"city":{"aqi":"44","pm25":"13"}},
            "daily_forecast":[{"date":"2016-08-08","cond":{"txt_d":"晴"},"tmp":{"max":"34","min":"27"}}],
            "now":{"tmp":"29","cond":{"txt":"晴"}},
            "suggestion":{"comf":{"txt":"1"},"cw":{"txt":"2"},"sport":{"txt":"3"}}}

            在把以上数据GSon解析

            */
}
