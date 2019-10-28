package com.example.huhaichang.weather3.tanqiu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.huhaichang.weather3.R;

import java.util.Timer;
import java.util.TimerTask;

public class TanqiuActivity extends AppCompatActivity {
    private TanqiuView tanqiuView;
    private LinearLayout root;
    DisplayMetrics metrics;
    private float qiuSpeedY=25;  //球的速度
    private float qiuSpeedX=80;  //球的速度
    private  float x=150f,y;  //板的x初始化y坐标
    private float banHeight=20f; //板高   //先不用了
    private float banWidth; //板宽
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanqiu);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取窗口管理器 得到屏幕高低
        WindowManager windowManager =getWindowManager();
        Display display =windowManager.getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        root =findViewById(R.id.ll_tanQiu);
        y =metrics.heightPixels-100;     //板初始位置;
        banWidth =metrics.widthPixels/5;   //板宽为屏幕的1/4;
        tanqiuView = new TanqiuView(this);
        tanqiuView.a=x;
        tanqiuView.b=y;
        tanqiuView.c=x+banWidth;
        tanqiuView.d=y+20f;
        tanqiuView.pinmukuang =metrics.widthPixels;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //小球坐标改变
                if(tanqiuView.x<=40||tanqiuView.x>=metrics.widthPixels-40){    //碰左右边框  //球等下等于40
                    qiuSpeedX= -qiuSpeedX;
                }
                if(tanqiuView.y<40){//碰上边框
                    qiuSpeedY = -qiuSpeedY;
                }else  if((tanqiuView.y+40)>=y){   //碰球拍
                    if(tanqiuView.x<(tanqiuView.a+banWidth)&&tanqiuView.x>tanqiuView.a){
                        qiuSpeedY = -qiuSpeedY;
                    }else{
                        tanqiuView.isGameOver=true;
                        timer.cancel();
                    }
                }
                tanqiuView.y +=qiuSpeedY;
                tanqiuView.x +=qiuSpeedX;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tanqiuView.invalidate();
                    }
                });
            }
        },0,100);
        root.addView(tanqiuView);
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float tempX = tanqiuView.a - distanceX;     //X轴新位置
                if( tempX < 0 ) tempX = 0;                                                 //左边界
                if(tempX>=metrics.widthPixels-banWidth) tempX=metrics.widthPixels-banWidth;//加一个右边界
                tanqiuView.a = (int)tempX;
                tanqiuView.c=tanqiuView.a+banWidth;
                tanqiuView.invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
