package com.example.huhaichang.weather3;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用户第一次下载才会出现 以后都不出现了
    preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    if(preferences.getString("weather",null)!=null){
        Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
        startActivity(intent);
        finish();
    }
    }
}
