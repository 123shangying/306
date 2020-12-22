package com.james.motion.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.james.motion.About;
import com.james.motion.R;
import com.james.motion.SignActivity;
import com.james.motion.add.activity.Main;
import com.james.motion.add.activity.SettingActivity;
import com.james.motion.add.activity.UserInfoActivity;
import com.james.motion.add.view.MyInfoView;


public class MainActivity extends AppCompatActivity {
    ////



    /////
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/////
       //  super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
// 判断是不是首次登录
        if (preferences.getBoolean("firstStart", true)) {
            editor = preferences.edit();
// 将登录标志位设置为false，下次登录时不在显示引导页
            editor.putBoolean("firstStart", false);
            editor.apply();

//跳转到引导页
            Intent intent = new Intent();
            intent.setClass(this, GuidePageActivity.class);
            startActivity(intent);
            finish();



//如果不是首次登录 启动mainactivity加载项

            initView();


        }

        //////

        initView();
        WebView webView = (WebView) this.findViewById(R.id.webview);
        webView.loadUrl("\n" +
                "https://zhangsixiang.gitee.io/jintianzhongwuchidiansha/");
        //加上下面这段代码可以使网页中的链接不以浏览器的方式打开
        webView.setWebViewClient(new WebViewClient());
        //得到webview设置
        WebSettings webSettings = webView.getSettings();
        //允许使用javascript
        webSettings.setJavaScriptEnabled(true);
        //设置可自由缩放网页
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });

    }

    private void init() {
    }


    public void initView() {


        findViewById(R.id.sign_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SportsActivity.class));
            }
        });
        findViewById(R.id.tianqi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignActivity.class));
            }
        });
        findViewById(R.id.jilu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        });
        findViewById(R.id.settings_tab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, About.class));
            }
        });
        findViewById(R.id.wode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserInfoActivity.class));
            }
        });

    }
}
