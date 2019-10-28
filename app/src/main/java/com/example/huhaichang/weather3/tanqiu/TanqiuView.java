package com.example.huhaichang.weather3.tanqiu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by huhaichang on 2019/9/13.
 */

public class TanqiuView extends View {
    public float x =200f;
    public float y =100f; //初始小球位置
    private Paint paint =new Paint();
    public boolean isGameOver=false;
    public float a,b,c,d;
    public float pinmukuang;
    public TanqiuView(Context context) {
        super(context);
    }

    public TanqiuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isGameOver) {
            paint.setColor(Color.RED);
            paint.setTextSize(40f);
            canvas.drawText("游戏结束", pinmukuang / 2 - 100, 200f, paint);
        } else {
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);//去锯齿
            canvas.drawCircle(x, y, 40f, paint);//弄个圆  2个float（位置）1个float(圆度圆大小) 一个Paint（属性 颜色等）

            paint.setColor(Color.WHITE);
            canvas.drawRect(a, b, c, d, paint);
        }
    }


}
