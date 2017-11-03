package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.CustomImageSwipeAdapter;
import com.cnpinyin.lastchinese.constants.AllConstans;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DialogueImageSlider extends AppCompatActivity {


    ImageView imageView;
    Button prev, next;

    private int index = 0;

    ArrayList<String> cnchars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);


        imageView = (ImageView) findViewById(R.id.image_slider);
        prev = (Button) findViewById(R.id.prev);
        next= (Button) findViewById(R.id.next);


        final Intent intent = getIntent();
        final String cnchar = intent.getStringExtra("cnchar");


        for(int i = 0; i<cnchar.length(); i++){
            cnchars.add(cnchar.substring(i, i+1));
        }


        Glide.with(DialogueImageSlider.this).asGif().load("http://cnpinyin.com/pinyin/PYT/ch-gif/all-files-1.1/"+cnchars.get(index)+".gif").into(imageView);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                index -=1;
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
                if(index<cnchars.size()){

                    Glide.with(DialogueImageSlider.this).asGif().load("http://cnpinyin.com/pinyin/PYT/ch-gif/all-files-1.1/"+cnchars.get(index)+".gif").into(imageView);

                }else {
                    index = cnchars.size()-1;
                }


            }
        });


    }
}
