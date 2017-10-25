package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cnpinyin.lastchinese.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(300);
                    startActivity(new Intent(getApplicationContext(), VocabularyList.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();

    }
}
