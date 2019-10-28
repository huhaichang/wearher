package com.example.huhaichang.weather3;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.huhaichang.weather3.db.County;
import com.example.huhaichang.weather3.db.Province;
import com.example.huhaichang.weather3.gpsdb.City2;
import com.example.huhaichang.weather3.gpsdb.County2;
import com.example.huhaichang.weather3.gpsdb.Province2;
import com.example.huhaichang.weather3.gpsdb.Util;
import com.example.huhaichang.weather3.widget.OkhttpUtil;
import com.example.huhaichang.weather3.widget.ToastUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GPSActivity extends AppCompatActivity {
    private LocationClient mLocationClient;
    private String province,city,county;
    private TextView mTVArea;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private List<Province2> province2List;
    private List<City2> city2List;
    private List<County2> county2List;
    private ProgressDialog progressDialog;
    /**
     * 当 区名字缓存="" http
     * 当 区名字缓存!="" 且定位区!=缓存区   http
     * 正常情况定位区=缓存区 直接进行intent
     * */
    //http 目的：把定位区的区id放入缓存区
    /**
     * 当前任务1.获取当前定位省的 标记码  （待完成）
     * 当前任务2.获取当前定位市的 标记码  （待完成）
     * 当前任务3.获取当前定位区的区id  （待完成）
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_gps);
        mTVArea = findViewById(R.id.tv_area);
        //申请3个权限 列表add 变数组一起
        List<String> list = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED){
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //列表空就不用申请了 当需要申请时
        if(!list.isEmpty()){
            String as[]=list.toArray(new String[list.size()]);
            /**需要用数组*/
            ActivityCompat.requestPermissions(GPSActivity.this,as,2);//然后得跳转到选择
        }else {
            requestLocation();
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        //读取缓存的2个关键数据 无缝跳转
       String a= mSharedPreferences.getString("weatherCounty",null);
       String id = mSharedPreferences.getString("weather",null);
       if(a!=null){//除了首次打开
           Intent intent = new Intent(GPSActivity.this,WeatherActivity.class);
           mEditor.putString("weatherCounty",a);
           mEditor.putString("weather",id);
           mEditor.apply();
           startActivity(intent);
           finish();

       }else{
           progressDialog=new ProgressDialog(GPSActivity.this);
           progressDialog.setTitle("提示");
           progressDialog.setMessage("正在获取当前城市天气数据中...\n(请确保你打开了手机定位服务)");
           progressDialog.setCancelable(false);
           progressDialog.show();
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2:       // 硬性条件                    只有申请的一中权限 3中的话grantResults[0]grantResults[1]grantResults[2]
                //原来的   if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(grantResults.length>0){
                    //3种都判断 只要存在一个不行就不行  通过int[] grantResults
                    for(int m : grantResults){
                        if(m!=PackageManager.PERMISSION_GRANTED){
                            ToastUtil.showMsg(GPSActivity.this,"必须同意所有权限");
                            finish();
                            return;
                        }
                        //直接获取
                        requestLocation();
                    }
                }else{   //拒绝
                    ToastUtil.showMsg(GPSActivity.this,"发生未知错误");
                    finish();
                }
                break;
            default:
                break;
        }
    }
    private void requestLocation(){
        //直接获取
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);//要记得关闭
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            province =bdLocation.getProvince();
            city=bdLocation.getCity();
            county=bdLocation.getDistrict();         /**定位获取*/

            //第一次
            if(mSharedPreferences.getString("weatherCounty",null)==null){
                String id = getid(province,city,county,false);
                mEditor.putString("weatherCounty", county);
                mEditor.putString("weatherProvince", province);
                mEditor.putString("weatherCity", city);
                mEditor.apply();
                if(!id.equals("")) {
                    Intent intent = new Intent(GPSActivity.this, WeatherActivity.class);
                    mEditor.putString("weather", id);
                    mEditor.apply();
                    startActivity(intent);
                    progressDialog.cancel();
                    finish();
                }
            }else{   //以后发生变化 变更缓存 通知WeatherActivity重新执行GPSActivity
                if(!county.contains(mSharedPreferences.getString("weatherCounty",null))){  //位置发送改变
                    String id = getid(province,city,county,true);
                    if(!id.equals("555")) {
                        mEditor.putString("weather", id);
                        mEditor.putBoolean("isChange", true);
                        mEditor.putString("weatherCounty", county);
                        mEditor.putString("weatherProvince", province);
                        mEditor.putString("weatherCity", city);
                        mEditor.putBoolean("noCity",true);  //让自定义城市能够跳转
                        mEditor.apply();                 //改定位不卸载测试看看能不能行
                    }

                }
            }

        }
    }
    //传入String城市 返回Stringid
    private String getid(String sheng,String shi ,String name,boolean ischange){
        String id="";
        if(name.equals("集美区")) {
            id="CN101230206";
        }else if(name.equals("思明区")) {
            id="CN101230203";
        }else if(name.equals("湖里区")) {
            id="CN101230205";
        }else if(name.equals("翔安区")) {
            id="CN101230207";
        }else if(name.equals("海沧区")) {
            id="CN101230204";
        }else {
            //查询省数据库得到 省id（第一次就http） (搞数据库 毕竟1次就行了)
            //http 市 得到 市id
            // http 区 得到 id   (搞数据库 毕竟市内走比较多)
          getshengid(sheng,shi,name,ischange);
            /**在获取到countyid那边进行跳转并放入缓存*/

        }
        return id;
    }



   /**获取省id*/
    private void getshengid(String sheng,String shi,String name,boolean ischange){
        String shengid="";
        //判断是否有缓存数据
        province2List = LitePal.findAll(Province2.class);
        if(province2List.size()>0){
            for(Province2 province2:province2List){
                if(sheng.contains(province2.getProvinceName())){   //定位的包含province2.getProvinceName()
                    shengid = province2.getProvinceCode()+"";
                    Log.d("省id", shengid);//18
                    getshiid(shi,shengid,name,ischange);
                }
            }
        }else {
            //去服务器查询
                String address = "http://guolin.tech/api/china";
                queryFromServer(address, sheng, 1, "0",shi,name,ischange); //保证执行一次
        }
    }

    /**获取市id*/
    private void getshiid(String shi,String shengid,String name,boolean ischange){
        String shiid ="";
        //直接http
        city2List =LitePal.where("provinceId = ?",String.valueOf(shengid)).find(City2.class);
        if(city2List.size()>0) {
            for (City2 city2 : city2List) {
                if (shi.contains(city2.getCityName())) {
                    shiid = city2.getCityCode() + "";
                    Log.d("市id", shiid);//139
                    getquid(shengid,shiid,name,ischange);
                }
            }
        }else{
            String address = "http://guolin.tech/api/china/" + shengid;
            queryFromServer(address, "", 2, shengid,shi,name,ischange);
        }
    }
    /**获取
     * 区天气id*/
    private void getquid(String shengid,String shiid,String county,boolean ischange){
            String weatherid="";
            county2List=LitePal.where("cityid = ?",String.valueOf(shiid)).find(County2.class);
            if(county2List.size()>0){
                for(County2 county2: county2List){
                    if(county.contains(county2.getCountyName())){
                        weatherid = county2.getWeatherId();
                        Log.d("区天气id", weatherid);//同安的CN101230202
                        mEditor.putString("weatherCounty", county);
                        mEditor.putString("weather", weatherid);
                        mEditor.putBoolean("isChange", ischange);
                        mEditor.apply();
                        if(!ischange){
                            progressDialog.cancel();
                        }
                        Intent intent = new Intent(GPSActivity.this,WeatherActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }else{
                String address = "http://guolin.tech/api/china/" + shengid+"/"+shiid;
                queryCountyFromServer(address,shiid,shengid,county,ischange);
            }

    }

    private  void queryFromServer(String address, final String sheng, final int type, final String shengid, final String shi, final String name, final boolean ischange){
        OkhttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showMsg(GPSActivity.this,"加载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                boolean result =false;
                //调用方法放到数据库 如果正常result=true
                if(type==1) {
                    result = Util.handleProvinceResponse(responseData);
                }else if(type==2){
                    result = Util.handleCityResponse(responseData,shengid);
                }
                if(result){
                    if(type==1){
                        getshengid(sheng,shi,name,ischange);
                    }else{
                        getshiid(shi,shengid,name,ischange);
                    }//此时数据库有数据了
                }
            }
        });
    }
    private  void queryCountyFromServer(String address, final String shiid, final String shengid, final String county, final boolean ischange){
        OkhttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showMsg(GPSActivity.this,"加载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                boolean result =false;
                result = Util.handleCountyResponse(responseData,shiid);
                if(result){
                    getquid(shengid,shiid,county,ischange);
                }

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
