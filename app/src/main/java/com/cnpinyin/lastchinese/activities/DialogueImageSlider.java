package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cnpinyin.lastchinese.R;

import java.util.ArrayList;

public class DialogueImageSlider extends AppCompatActivity {

    ImageView imageView;
    Button prev, next;
    TextView cross;


    private int index = 0;

    ArrayList<String> cnchars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);


        cross =  (TextView) findViewById(R.id.btn_cross);
        imageView = (ImageView) findViewById(R.id.image_slider);
        prev = (Button) findViewById(R.id.btn_prev);
        next= (Button) findViewById(R.id.btn_next);


        final Intent intent = getIntent();
        final String cnchar = intent.getStringExtra("cnchar");


        for(int i = 0; i<cnchar.length(); i++){
            cnchars.add(cnchar.substring(i, i+1));
        }


        if(cnchars.size() == 1){
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }else{
            prev.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Glide.with(DialogueImageSlider.this).asGif().load("http://cnpinyin.com/pinyin/PYT/ch-gif/all-files-1.1/"+cnchars.get(index)+".gif").into(imageView);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                index -=1;
                next.setVisibility(View.VISIBLE);

                if(index==0){
                    prev.setVisibility(View.GONE);
                }

                if(index>=0){

                    Glide.with(DialogueImageSlider.this).asGif().load("http://cnpinyin.com/pinyin/PYT/ch-gif/all-files-1.1/"+cnchars.get(index)+".gif").into(imageView);
                }else {
                    index = 0;

                }

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                index+=1;

                prev.setVisibility(View.VISIBLE);


                if(index == cnchars.size()-1)
                {
                    next.setVisibility(View.GONE);
                }


                if(index<cnchars.size()){

                    Glide.with(DialogueImageSlider.this).asGif().load("http://cnpinyin.com/pinyin/PYT/ch-gif/all-files-1.1/"+cnchars.get(index)+".gif").into(imageView);

                }else {
                    index = cnchars.size()-1;
                }


            }
        });


    }
}
