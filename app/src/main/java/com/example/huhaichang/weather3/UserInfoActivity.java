package com.example.huhaichang.weather3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserInfoActivity extends AppCompatActivity {
    private Button bt_back ;
    private TextView tv_save;
    private EditText et_mail,et_name;
    private EditText et_birth;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String mail,name;
    private String birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        bt_back = findViewById(R.id.bt_back);
        tv_save = findViewById(R.id.tv_save);
        et_mail = findViewById(R.id.et_mail);
        et_name = findViewById(R.id.et_name);
        et_birth = findViewById(R.id.et_birth);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        name =mSharedPreferences.getString("userName", "");
        mail =mSharedPreferences.getString("userMail", "");//用户名邮件
        birth= mSharedPreferences.getString("userBirth", "");//用户名邮件
        if(!mail.equals("")){
            et_mail.setText(mail);
            et_name.setText(name);
        }else{
            et_mail.setText("123456789@qq.com");
            et_name.setText("HHC");
        }
        if(!birth.equals("")){
            et_birth.setText(birth);
        }
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this,WeatherActivity.class);
                intent.putExtra("openDraw","1");
                startActivity(intent);
                finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**存储数据*/
                mEditor.putString("userName",et_name.getText().toString());
                mEditor.putString("userMail",et_mail.getText().toString());
                mEditor.putString("userBirth",et_birth.getText().toString());
                mEditor.apply();
                Intent intent = new Intent(UserInfoActivity.this,WeatherActivity.class);
                intent.putExtra("openDraw","1");
                startActivity(intent);
                finish();
            }
        });
    }
}
