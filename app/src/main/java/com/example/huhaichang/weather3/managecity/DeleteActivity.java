package com.example.huhaichang.weather3.managecity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huhaichang.weather3.R;
import com.example.huhaichang.weather3.WeatherActivity;

import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {
    private LinearLayout mLLDelete;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView mTvPoint;
    private Button mBtok;
    private String x;
    private List<String> list = new ArrayList<>();  //放入内容
    private ImageView back; //背景

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //系统标题背景覆盖掉(android:以上)
        if(Build.VERSION.SDK_INT>=21){
            View decotView = getWindow().getDecorView();
            decotView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_delete);
        mLLDelete = findViewById(R.id.ll_delete);
        back = findViewById(R.id.iv_deleteBackground);
        sharedPreferences = getSharedPreferences("Point",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        list.add(sharedPreferences.getString("11",""));
        list.add(sharedPreferences.getString("22",""));
        list.add(sharedPreferences.getString("33",""));
        list.add(sharedPreferences.getString("44",""));
        mLLDelete.removeAllViews();
        for(int i = 0;i<list.size();i++) {
            x=list.get(i);
            if(!x.equals("")){   //存在的添加进去
                View view = LayoutInflater.from(DeleteActivity.this).inflate(R.layout.points_item, mLLDelete, false);
                mTvPoint = view.findViewById(R.id.tv_point);
                mBtok = view.findViewById(R.id.bt_ok);
                mTvPoint.setText(x);
                final int finalI = i;
                mBtok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString(""+ (finalI+1),"");
                        editor.putString(""+ (finalI+1)+(finalI+1),"");
                        editor.putInt("qww",0);//删除后回到第一个
                        editor.apply();
                        Intent intent =new Intent(DeleteActivity.this,WeatherActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                mLLDelete.addView(view);
            }
        }
        //加载背景
        String photoAdress ="http://cn.bing.com/th?id=OHR.KelpKeepers_ROW3286873611_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
        Glide.with(DeleteActivity.this).load(photoAdress).into(back);
    }
}
