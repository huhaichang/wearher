package com.example.huhaichang.weather3.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.huhaichang.weather3.R;
import com.example.huhaichang.weather3.widget.ToastUtil;

public class BirthInfoActivity extends AppCompatActivity {
    private Button bt_back;
    private TextView tv_save,tv_age,tv_starMap;
    private NumberPicker np_year,np_mon,np_day;
    private String[] years = new String[121];
    private String[] months = new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
    private String[] days = new String[31];
    int m=1899;
    int j=1;
    /**缓存数据有 20岁(√)  射手座  1998-12-07  3个String      3个初始int*/
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String age;
    private int int_year,int_month,int_day;
    private String year,mon,day,starMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth_info);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        bt_back = findViewById(R.id.bt_back);
        tv_save = findViewById(R.id.tv_save);
        np_year = findViewById(R.id.np_year);
        np_mon = findViewById(R.id.np_mon);
        np_day = findViewById(R.id.np_day);
        tv_age= findViewById(R.id.tv_age);
        tv_starMap= findViewById(R.id.tv_starMap);
        for(int i=0;i<=120;i++){
            String s= String.valueOf(m)+"年";
            years[i] =s;
            m++;
        }
        for(int i=0;i<=30;i++){
            String s= String.valueOf(j)+"日";
            days[i] =s;
            j++;
        }
        np_year.setDisplayedValues(years);
        np_year.setMaxValue(years.length-1);
        np_mon.setDisplayedValues(months);
        np_mon.setMaxValue(months.length-1);
        np_day.setDisplayedValues(days);
        np_day.setMaxValue(days.length-1);
        np_year.setWrapSelectorWheel(false);
        np_mon.setWrapSelectorWheel(false);
        np_day.setWrapSelectorWheel(false);
        //获取缓存
        age = mSharedPreferences.getString("userAge",null);
        int_year = mSharedPreferences.getInt("int_year",100);
        int_month =mSharedPreferences.getInt("int_month",1);
        int_day = mSharedPreferences.getInt("int_day",1);
        starMap =mSharedPreferences.getString("userStar",null);
        //初始化
        if(age==null){ /**没缓存的时候 1999年1月1日*/
            np_year.setValue(100); //1899+100 =1999年
            tv_age.setText("20岁");
        }else{
            tv_age.setText(age);
            np_year.setValue(int_year);
            np_mon.setValue(int_month);
            np_day.setValue(int_day);
            tv_starMap.setText(starMap);
        }
        year=np_year.getDisplayedValues()[int_year];
        mon=np_mon.getDisplayedValues()[int_month];
        day=np_day.getDisplayedValues()[int_day];
        /**月的滑动监听 更新日28 30 31*/
        np_mon.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if(scrollState==SCROLL_STATE_IDLE){//停止滑动
                    String q =view.getDisplayedValues()[view.getValue()];
                    if(q.equals("2月")){
                                np_day.setMaxValue(days.length-4);
                    }else if(q.equals("4月")||q.equals("6月")||q.equals("9月")||q.equals("11月")){
                        np_day.setMaxValue(days.length-2);
                    }
                    else {
                        np_day.setMaxValue(days.length-1);

                    }
                    mon=view.getDisplayedValues()[view.getValue()];
                    starMap = getStar(view.getValue(),np_day.getValue());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_starMap.setText(starMap);
                        }
                    });
                }
            }
        });
        /**年的滑动监听 更新年龄*/
        np_year.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if(scrollState==SCROLL_STATE_IDLE) {//停止滑动
                    int age1=(120-np_year.getValue());  //100=1999年  等于20岁    90=1989年  等于30岁
                    age=String.valueOf(age1)+"岁";
                    year=view.getDisplayedValues()[view.getValue()];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_age.setText(age);
                        }
                    });
                }
            }
        });
        np_day.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if(scrollState==SCROLL_STATE_IDLE) {//停止滑动
                    day=view.getDisplayedValues()[view.getValue()];
                    starMap = getStar(np_mon.getValue(),view.getValue());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_starMap.setText(starMap);
                        }
                    });
                }
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BirthInfoActivity.this,UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putInt("int_year",np_year.getValue());
                mEditor.putInt("int_month",np_mon.getValue());
                mEditor.putInt("int_day",np_day.getValue());
                mEditor.putString("userAge",String.valueOf(age));
                mEditor.putString("userBirth",year+mon+day);
                mEditor.putString("userStar",starMap);
                mEditor.apply();
                Intent intent = new Intent(BirthInfoActivity.this,UserInfoActivity.class);
                startActivity(intent);finish();
            }
        });
    }
    private String getStar(int m,int d) {
        if ((m == 0 && d > 19) || (m == 1 && d < 19)) {
            return "水瓶座";
        } else if ((m == 1 && d > 18) || (m == 2 && d < 20)) {
            return "双鱼座";
        }else if ((m==2 && d>19) || (m==3 && d<20)) {
            return "白羊座";
        }else if ((m==3 && d>19) || (m==4 && d<21)) {
            return "金牛座";
        }else if ((m==4 && d>20) || (m==5 && d<21)) {
            return "双子座";
        }else if ((m==5 && d>20) || (m==6 && d<22)) {
            return "巨蟹座";
        }else if ((m==6 && d>21) || (m==7 && d<23)) {
            return "狮子座";
        }else if ((m==7 && d>22) || (m==8 && d<23)) {
            return "处女座";
        }else if ((m==8 && d>22) || (m==9 && d<23)) {
            return "天秤座";
        }else if ((m==9 && d>22) || (m==10 && d<22)) {
            return "天蝎座";
        }else if ((m==10 && d>21) || (m==11 && d<21)) {
            return "射手座";
        }else {
            return "摩羯座";
        }

    }
}
