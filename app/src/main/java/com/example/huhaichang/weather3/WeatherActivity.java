package com.example.huhaichang.weather3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huhaichang.weather3.gson.Forecast;
import com.example.huhaichang.weather3.gson.Weather;
import com.example.huhaichang.weather3.managecity.ADDActivity;
import com.example.huhaichang.weather3.managecity.DeleteActivity;
import com.example.huhaichang.weather3.service.MyService;
import com.example.huhaichang.weather3.tanqiu.TanqiuActivity;
import com.example.huhaichang.weather3.widget.MFragmentPagerAdapter;
import com.example.huhaichang.weather3.widget.OkhttpUtil;
import com.example.huhaichang.weather3.widget.ToastUtil;
import com.example.huhaichang.weather3.widget.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//先去服务器返回一个json数据在用sharePreference存储   然后解析
public class WeatherActivity extends AppCompatActivity {

    private Button mBtGoCity;
    private TextView mTVCityName;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor,editor;
    private ImageView mIVBackground,mIVDingWei;
    private ImageView mIVFly;  //蝴蝶
    private String mWeatherId;
    private String cityName;  //定位城市名
    private String provinceName,shiName;//省 市名字
    //viewPaper
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private FragmentManager fragmentManager;

    private LinearLayout mLLayout;
    private List<View> pointList =new ArrayList<>();
    //管理城市 （加减）
    private PopupWindow mpopupWindow;
    private SharedPreferences sharedPreferences;
    private String a,b,c,d;   //存入城市id
    private String as,bs,cs,ds;   //存入城市名
    private String mail,name;//存入个人信息
    private int page;//记录退出时页数

    private boolean ischange; //如果定位信息发生改变回到GPSActivity(延时3s执行毕竟定位需要时间)

    private DrawerLayout drawerLayout;//滑动窗口
    private NavigationView navigationView;
    private TextView mTVMail,mTVName;
    private ImageView mIvSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //系统标题背景覆盖掉(android:以上)
        if(Build.VERSION.SDK_INT>=21){
        View decotView = getWindow().getDecorView();
        decotView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        mTVCityName = findViewById(R.id.tv_cityName);
        mIVBackground = findViewById(R.id.iv_background);
        mBtGoCity = findViewById(R.id.bt_goCity);
        mViewPager = findViewById(R.id.vp_details_top);
        mLLayout = findViewById(R.id.ll_details_top_dot);
        mIVDingWei = findViewById(R.id.iv_dingWei);
        mIVFly = findViewById(R.id.iv_fly);
        drawerLayout = findViewById(R.id.dl_1);
        navigationView =findViewById(R.id.nav_view);
       // navigationView.setCheckedItem(R.id.nav_add);//初始化默认第一个Call 应为Group了 所以你选择的会变颜色
        final View view = navigationView.getHeaderView(0);
        mTVMail = view.findViewById(R.id.mail);
        mTVName = view.findViewById(R.id.name);
        mIvSet = view.findViewById(R.id.iv_set);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        sharedPreferences = getSharedPreferences("Point",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        a=sharedPreferences.getString("1", "");//城市id
        b=sharedPreferences.getString("2", "");
        c=sharedPreferences.getString("3", "");
        d=sharedPreferences.getString("4", "");
        as=sharedPreferences.getString("11", "");//城市名
        bs=sharedPreferences.getString("22", "");
        cs=sharedPreferences.getString("33", "");
        ds=sharedPreferences.getString("44", "");
        page = sharedPreferences.getInt("qww",0);//退出时页数
        name =mSharedPreferences.getString("userName", "");
        mail =mSharedPreferences.getString("userMail", "");//用户名邮件
        Intent intent = getIntent();
        String ifopen = intent.getStringExtra("openDraw");
        if(ifopen!=null){
            drawerLayout.openDrawer(GravityCompat.START);
        }

        if(!mail.equals("")){
            mTVMail.setText(mail);
            mTVName.setText(name);
        }else{
            mTVName.setText("HHC");
            mTVMail.setText("123456789@qq.com");
        }

        mIvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,UserInfoActivity.class);
                startActivity(intent);
            }
        });
        AnimationDrawable anim = (AnimationDrawable) mIVFly.getBackground();
        anim.start();



        String bingPic = mSharedPreferences.getString("bing_pic",null);
        //每次一开启app服务会去请求放入数据库 所以是最新的
        if(bingPic!=null){
            Glide.with(WeatherActivity.this).load(bingPic).into(mIVBackground);
        }else{
            loadBingPic();//下载后第一次打开
        }
        //每次一开启app服务会去执行（如果有缓存就重新放入数据库）所以是最新的
            mWeatherId = mSharedPreferences.getString("weather",null);
            cityName = mSharedPreferences.getString("weatherCounty",null);
            provinceName=mSharedPreferences.getString("weatherProvince",null);
            shiName= mSharedPreferences.getString("weatherCity",null);
            if(page==0){
        mIVDingWei.setVisibility(View.VISIBLE);
            }
            mTVCityName.setText(cityName);
                 if(page!=0){
        mTVCityName.setText(sharedPreferences.getString(""+page+page, ""));
        }
        /**拿给viewpaper的fragment*/
        fragmentArrayList.add(new CityFragment(mWeatherId));
        if(!sharedPreferences.getString("1","").equals("")) {
            fragmentArrayList.add(new CityFragment(a));
        }
        if(!sharedPreferences.getString("2","").equals("")) {
            fragmentArrayList.add(new CityFragment(b));
        }
        if(!sharedPreferences.getString("3","").equals("")) {
            fragmentArrayList.add(new CityFragment(c));
        }
        if(!sharedPreferences.getString("4","").equals("")) {
            fragmentArrayList.add(new CityFragment(d));
        }
        fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));
        for(int i =0;i<fragmentArrayList.size();i++){
            View point = new View(WeatherActivity.this);
            if(i==page){
                point.setBackgroundResource(R.drawable.yuandian);//view为自定义原点
            }else{
                point.setBackgroundResource(R.drawable.yuandian1);
            }
            LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(14,14); //设置圆大小
            if(i>0){
                params.leftMargin=10;  //圆间隔
            }
            point.setLayoutParams(params);
            pointList.add(point);
            pointList.get(0).setVisibility(View.VISIBLE);
            mLLayout.addView(point);
        }
        if(fragmentArrayList.size()==1){
            pointList.get(0).setVisibility(View.GONE);
        }
        mViewPager.setCurrentItem(page);//设置初始显示位置
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(new My1OnPageChangeListener());

        /**添加删除City*/
        mBtGoCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.layout_popup_window,null);
                TextView add =view.findViewById(R.id.tv_add);
                final TextView delete =view.findViewById(R.id.tv_delete);
                delete.setVisibility(View.VISIBLE);
                if(a.equals("")&&b.equals("")&&c.equals("")&&d.equals("")) {
                    delete.setVisibility(View.GONE);
                }//只有1个就不显示删除按钮
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(a.equals("")||b.equals("")||c.equals("")||d.equals("")){  //存在空
                            Intent intent = new Intent(WeatherActivity.this,ADDActivity.class);
                            startActivity(intent);
                        }else{
                            ToastUtil.showMsg(WeatherActivity.this,"最大上限为5");
                        }
                        mpopupWindow.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent(WeatherActivity.this, DeleteActivity.class);
                            startActivity(intent);
                        mpopupWindow.dismiss();
                    }
                });
                mpopupWindow =new PopupWindow(view,mBtGoCity.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
                mpopupWindow.setOutsideTouchable(true);
                mpopupWindow.setFocusable(true);
                mpopupWindow.showAsDropDown(mBtGoCity);*/
              drawerLayout.openDrawer(GravityCompat.START);

    }
});
        /**定位改变 重新加载app*/
        final boolean mycity = mSharedPreferences.getBoolean("noCity",false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ischange = mSharedPreferences.getBoolean("isChange",false);
                Log.d("aa",mycity+"");
                if(ischange){
                    mEditor.putBoolean("isChange",false);
                    mEditor.apply();
                    if(mycity) {
                        Intent intent = new Intent(WeatherActivity.this, GPSActivity.class);
                        mEditor.putBoolean("noCity",false);
                        mEditor.apply();
                        startActivity(intent);
                    }
                }
            }
        }, 3000);

        /**小游戏*/
        mIVFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.layout_popup_window,null);
                TextView add =view.findViewById(R.id.tv_add);
                add.setText("弹球小游戏");
                add.setTextSize(20);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WeatherActivity.this, TanqiuActivity.class);
                        startActivity(intent);
                        mpopupWindow.dismiss();
                    }
                });
               // final TextView delete =view.findViewById(R.id.tv_delete);
                mpopupWindow =new PopupWindow(view,400, ViewGroup.LayoutParams.WRAP_CONTENT);
                mpopupWindow.setOutsideTouchable(true);
                mpopupWindow.setFocusable(true);
                mpopupWindow.showAsDropDown(mIVFly,0,10);
            }
        });

        /**滑动菜单跳转*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent=null;
                switch (item.getItemId()){
                    case R.id.nav_game:
                        //添加对话框
                        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                        builder.setTitle("休闲弹球小游戏").setMessage("是否开始游戏").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                 Intent intent = new Intent(WeatherActivity.this, TanqiuActivity.class);
                                startActivity(intent);
                                drawerLayout.closeDrawers();
                            }
                        }).show();
                        break;
                    case R.id.nav_add:
                        if(a.equals("")||b.equals("")||c.equals("")||d.equals("")){  //存在空
                            intent = new Intent(WeatherActivity.this,ADDActivity.class);
                            startActivity(intent);
                        }else{
                            ToastUtil.showMsg(WeatherActivity.this,"最大上限为5");
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_delete:
                        if(a.equals("")&&b.equals("")&&c.equals("")&&d.equals("")) {
                            ToastUtil.showMsg(WeatherActivity.this,"没有城市可以移除");
                        }else{
                            intent = new Intent(WeatherActivity.this,DeleteActivity.class);
                            startActivity(intent);
                        }
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_location:
                        Snackbar.make(view,"当前城市为："+cityName,Snackbar.LENGTH_SHORT).setAction("具体位置", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(WeatherActivity.this,provinceName+shiName+cityName,Toast.LENGTH_LONG).show();
                            }
                        }).show();
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }


    //请求返回图片地址
    private void loadBingPic(){
        String photoUrl ="http://guolin.tech/api/bing_pic";
        OkhttpUtil.sendHttpRequest(photoUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    final String photoAdress = response.body().string();
                    //存入sharedperference数据
                mEditor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                mEditor.putString("bing_pic",photoAdress);
                mEditor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(photoAdress).into(mIVBackground);
                    }
                });
            }
        });
    }
    public class My1OnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //数量小固定数量 就用这个(弄最大值为5个)
        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0://滑动到页面1
                    mIVDingWei.setVisibility(View.VISIBLE);
                    editor.putInt("qww",0);
                    editor.apply();
                    mTVCityName.setText(cityName);
                    asd();
                    pointList.get(0).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 1://滑动到页面2
                    mIVDingWei.setVisibility(View.GONE);
                    editor.putInt("qww",1);
                    editor.apply();
                    mTVCityName.setText(as);
                    asd();
                    pointList.get(1).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 2://滑动到页面3
                    mIVDingWei.setVisibility(View.GONE);
                    editor.putInt("qww",2);
                    editor.apply();
                    asd();
                    mTVCityName.setText(bs);
                    pointList.get(2).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 3://滑动到页面4
                    mIVDingWei.setVisibility(View.GONE);
                    editor.putInt("qww",3);
                    editor.apply();
                    mTVCityName.setText(cs);
                    asd();
                    pointList.get(3).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 4://滑动到页面5
                    mIVDingWei.setVisibility(View.GONE);
                    editor.putInt("qww",4);
                    editor.apply();
                    mTVCityName.setText(ds);
                    asd();
                    pointList.get(4).setBackgroundResource(R.drawable.yuandian);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    private void asd(){
        for(int i =0;i<fragmentArrayList.size();i++) {
            pointList.get(i).setBackgroundResource(R.drawable.yuandian1);
        }
    }
//服务不开了
  /*  private void showWeatherInfo(Weather weather){
        Intent intent = new Intent(WeatherActivity.this,MyService.class);//不开服务
        startService(intent);
    }*/
}
