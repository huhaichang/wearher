package com.example.huhaichang.weather3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huhaichang.weather3.widget.MFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Test1Activity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private FragmentManager fragmentManager;
    //点
    private LinearLayout mLLayout;
    private List<View> pointList =new ArrayList<>();
    private TextView cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        mViewPager = findViewById(R.id.vp_details_top);
        mLLayout = findViewById(R.id.ll_details_top_dot);
        cityName = findViewById(R.id.tv_cityName);
        fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));
        for(int i =0;i<fragmentArrayList.size();i++){
            View point = new View(Test1Activity.this);
            if(i==0){
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
            mLLayout.addView(point);
        }
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(new My1OnPageChangeListener());

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
                    cityName.setText("北京");
                    asd();
                    pointList.get(0).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 1://滑动到页面2
                    cityName.setText("集美");
                    asd();
                    pointList.get(1).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 2://滑动到页面3
                    cityName.setText("永定");
                    asd();
                    pointList.get(2).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 3://滑动到页面4
                    asd();
                    pointList.get(3).setBackgroundResource(R.drawable.yuandian);
                    break;
                case 4://滑动到页面4
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
}
