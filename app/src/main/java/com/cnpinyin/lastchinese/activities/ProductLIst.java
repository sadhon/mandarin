package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cnpinyin.lastchinese.R;

public class ProductLIst extends AppCompatActivity {
    Button btnVocList, btnSenAndDia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        btnVocList = (Button) findViewById(R.id.btn_voc);
        btnSenAndDia = (Button) findViewById(R.id.btn_sen);

        btnVocList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VocabularyList.class));

            }
        });

        btnSenAndDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SentencesAndDialogue.class));
            }
        });
    }
}
