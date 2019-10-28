package com.example.huhaichang.weather3.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by huhaichang on 2019/6/11.
 */

public class ToastUtil {
    public static Toast mToast;
    public static void  showMsg(Context context,String msg){
        if(mToast==null){                          //保证2s内只第一次点击有效
            mToast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }
        else{
         mToast.setText(msg);
        }
        mToast.show();
    }
}
