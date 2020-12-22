package com.james.motion.add.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.james.motion.R;
import com.james.motion.add.utils.MD5Utils;
import com.james.motion.ui.activity.GuidePageActivity;
import com.james.motion.ui.activity.MainActivity;


public class Denglu extends AppCompatActivity {
    private String userName,psw,spPsw;
    private EditText et_user_name,et_psw;
    private Handler handler;
    //private Runnable runnable;

    public Denglu(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denglu);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();




         //   super.onCreate(savedInstanceState);
            // requestWindowFeature(Window.FEATURE_NO_TITLE);
            //   setContentView(R.layout.activity_login);
        SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
// 判断是不是首次登录
            if (preferences.getBoolean("firstStart", true)) {
                SharedPreferences.Editor editor = preferences.edit();
// 将登录标志位设置为false，下次登录时不在显示引导页
                editor.putBoolean("firstStart", false);
                editor.apply();

//跳转到引导页
                Intent intent = new Intent();
                intent.setClass(this, GuidePageActivity.class);
                startActivity(intent);
                finish();



//如果不是首次登录 启动mainactivity加载项

                init();


    }
        }




    /**
     * 获取界面控件
     */
    private void init(){
        TextView tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("登录");
        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        TextView tv_register = (TextView) findViewById(R.id.tv_register);
        TextView tv_find_psw = (TextView) findViewById(R.id.tv_find_psw);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        et_user_name=(EditText) findViewById(R.id.et_user_name);
        et_psw=(EditText) findViewById(R.id.et_psw);
        tv_back.setOnClickListener(v -> Denglu.this.finish());
        //立即注册控件的点击事件
        tv_register.setOnClickListener(v -> {
            Intent intent=new Intent(Denglu.this,RegisterActivity.class);
            startActivityForResult(intent, 1);
        });


        //找回密码控件的点击事件
        tv_find_psw.setOnClickListener(v -> {
            Intent intent=new Intent(Denglu.this,FindPswActivity.class);
            startActivity(intent);
        });
        //登录按钮的点击事件
        btn_login.setOnClickListener(v -> {
            userName=et_user_name.getText().toString().trim();
            psw=et_psw.getText().toString().trim();
            String md5Psw= MD5Utils.md5(psw);
            spPsw=readPsw(userName);
            if(TextUtils.isEmpty(userName)){
             //   Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                Toast.makeText(Denglu.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(psw)){
                //   Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                Toast.makeText(Denglu.this, "请输入密码", Toast.LENGTH_SHORT).show();
            }else if(md5Psw.equals(spPsw)){
                Toast.makeText(Denglu.this, "您以开启阴阳之门！", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Denglu.this, MainActivity.class);
                startActivity(intent);

                //保存登录状态
                saveLoginStatus(userName);
                Intent data=new Intent();
                data.putExtra("isLogin",true);
                setResult(RESULT_OK,data);
                Denglu.this.finish();
            }else if((spPsw!=null&&!TextUtils.isEmpty(spPsw)&&!md5Psw.equals(spPsw))){
               // Toast.makeText(Denglu.this, "不好意思，输错了呢！——哪里错了？不告诉你！！！", Toast.LENGTH_SHORT).show();
                Toast.makeText(Denglu.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
            }else{
               // Toast.makeText(Denglu.this, "不好意思，输错了呢！——哪里错了？不告诉你！！！", Toast.LENGTH_SHORT).show();
                  Toast.makeText(Denglu.this, "此用户名不存在", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     *从SharedPreferences中根据用户名读取密码
     */
    private String readPsw(String userName){
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString(userName, "");
    }
    /**
     *保存登录状态和登录用户名到SharedPreferences中
     */
    private void saveLoginStatus(String userName){
        //loginInfo表示文件名
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编辑器
        editor.putBoolean("isLogin", true);//存入boolean类型的登录状态
        editor.putString("loginUserName", userName);//存入登录状态时的用户名
        editor.apply();//提交修改

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //从注册界面传递过来的用户名
            String userName =data.getStringExtra("userName");
            if(!TextUtils.isEmpty(userName)){
                et_user_name.setText(userName);
                //设置光标的位置
                et_user_name.setSelection(userName.length());
            }
        }
    }


}