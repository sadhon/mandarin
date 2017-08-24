package com.cnpinyin.lastchinese.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cnpinyin.lastchinese.R;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_layout);
    }
}
