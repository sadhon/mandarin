package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cnpinyin.lastchinese.R;

public class ProductLIst extends AppCompatActivity {
    Button btnVoc, btnSen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        btnVoc = (Button) findViewById(R.id.btn_voc);
        btnSen = (Button) findViewById(R.id.btn_sen);

        btnVoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VocabularyList.class));

            }
        });

        btnSen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SentencesAndDialogue.class));
            }
        });
    }
}
