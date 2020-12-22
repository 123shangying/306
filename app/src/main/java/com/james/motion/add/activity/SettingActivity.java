package com.james.motion.add.activity;
/*
 * author : shangying
 * date   : 2019/10/26
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.james.motion.R;


public class SettingActivity extends AppCompatActivity {
    public static SettingActivity instance;

    static {
        instance = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        instance=this;
        init();
    }
    /**
     * 获取界面控件
     */
    private void init(){
        TextView tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("设置");
        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        RelativeLayout rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        RelativeLayout rl_modify_psw = (RelativeLayout) findViewById(R.id.rl_modify_psw);
        RelativeLayout rl_security_setting = (RelativeLayout) findViewById(R.id.rl_security_setting);
        RelativeLayout rl_exit_login = (RelativeLayout) findViewById(R.id.rl_exit_login);
        tv_back.setOnClickListener(v -> SettingActivity.this.finish());
        //修改密码的点击事件
        rl_modify_psw.setOnClickListener(v -> {
            Intent intent=new Intent(SettingActivity.this,ModifyPswActivity.class);
            startActivity(intent);
        });
        //设置密保的点击事件
        rl_security_setting.setOnClickListener(v -> {
            Intent intent=new Intent(SettingActivity.this,FindPswActivity.class);
            intent.putExtra("from", "security");
            startActivity(intent);
        });
        //退出登录的点击事件
        rl_exit_login.setOnClickListener(v -> {
            Toast.makeText(SettingActivity.this, "退出登录成功", Toast.LENGTH_SHORT).show();
            clearLoginStatus();//清除登录状态和登录时的用户名
            //退出登录成功后把退出成功的状态传递到MainActivity中
            Intent data =new Intent();
            data.putExtra("isLogin", false);
            setResult(RESULT_OK, data);
            SettingActivity.this.finish();
        });
    }
    /**
     * 清除SharedPreferences中的登录状态和登录时的用户名
     */
    private void clearLoginStatus(){
        SharedPreferences sp=getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编辑器
        editor.putBoolean("isLogin", false);
        editor.putString("loginUserName", "");
        editor.apply();//提交修改
    }
}
