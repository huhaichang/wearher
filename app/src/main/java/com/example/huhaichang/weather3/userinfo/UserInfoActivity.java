package com.example.huhaichang.weather3.userinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huhaichang.weather3.R;
import com.example.huhaichang.weather3.WeatherActivity;

public class UserInfoActivity extends AppCompatActivity {
    private Button bt_back ;
    private TextView tv_save,tv_birth;
    private EditText et_mail,et_name;
    private RadioButton rb_man,rb_woman;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String mail,name;
    private String birth;

    private boolean isman;
    private RelativeLayout rl_photo,rl_birth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        bt_back = findViewById(R.id.bt_back);
        tv_save = findViewById(R.id.tv_save);
        et_mail = findViewById(R.id.et_mail);
        et_name = findViewById(R.id.et_name);
        tv_birth = findViewById(R.id.tv_birth);
        rl_photo = findViewById(R.id.rl_photo);
        rb_man = findViewById(R.id.rb_man);
        rb_woman = findViewById(R.id.rb_woman);
        rl_birth= findViewById(R.id.ll_birth);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        name =mSharedPreferences.getString("userName", "");
        mail =mSharedPreferences.getString("userMail", "");//用户名邮件
        birth = mSharedPreferences.getString("userBirth", "");
        isman = mSharedPreferences.getBoolean("sex",false);
        hintcursor();//隐藏邮箱的光标
        if(!isman){
            rb_woman.setChecked(true);
        }
        /**设置用户资料*/
        if(!mail.equals("")){
            et_mail.setText(mail);
            et_name.setText(name);
        }else{
            et_mail.setText("123456789@qq.com");
            et_name.setText("HHC");
        }
        if(!birth.equals("")){
            tv_birth.setText(birth);
        }

        /**返回*/
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(UserInfoActivity.this,WeatherActivity.class);
                intent.putExtra("openDraw","1");
                startActivity(intent);*/
                finish();
            }
        });
        /**保存信息*/
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**存储数据*/
                mEditor.putString("userName",et_name.getText().toString());
                mEditor.putString("userMail",et_mail.getText().toString());
                mEditor.putBoolean("sex",rb_man.isChecked());
                mEditor.apply();
                Intent intent = new Intent(UserInfoActivity.this,WeatherActivity.class);
                intent.putExtra("openDraw","1");
                startActivity(intent);
               // finish();
            }
        });

        /**编辑照片*/
        rl_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, TakePhotoActivity.class);
                startActivity(intent);
            }
        });
        rl_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this,BirthInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    private void hintcursor(){
        et_mail.setCursorVisible(false);
        et_mail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    et_mail.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
    }
}
