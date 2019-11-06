package com.james.motion.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.james.motion.About;
import com.james.motion.R;
import com.james.motion.SignActivity;


public class StyleActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);
        initView();
    }

    public void initView() {




        findViewById(R.id.sign_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StyleActivity.this, SportsActivity.class));
            }
        });
        findViewById(R.id.tianqi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StyleActivity.this, SignActivity.class));
            }
        });
        findViewById(R.id.jilu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StyleActivity.this, HomeActivity.class));
            }
        });
        findViewById(R.id.settings_tab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StyleActivity.this, About.class));
            }
        });



    }

}
