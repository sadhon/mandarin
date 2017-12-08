package com.cnpinyin.lastchinese.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.cnpinyin.lastchinese.R;

public class DbTest extends AppCompatActivity {

     public EditText url ;
     public EditText jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        url = (EditText) findViewById(R.id.url);
        jsonData = (EditText) findViewById(R.id.json_data);




    }


}
