package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cnpinyin.lastchinese.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView img = (ImageView) findViewById(R.id.splash_image);
        ImageView animateImg = (ImageView) findViewById(R.id.splash_animate_img);

        Glide.with(SplashActivity.this)
                .asBitmap()
                .load(R.drawable.splash_banner)
                .into(img);

        Glide.with(this)
                .asGif()
                .load(R.raw.splash_loading_img)
                .into(animateImg);

        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    startActivity(new Intent(getApplicationContext(), ProductLIst.class));
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
