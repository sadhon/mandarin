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
import com.cnpinyin.lastchinese.constants.AllConstans;

import java.util.ArrayList;

public class PopupForImage extends AppCompatActivity {

    ImageView imageView;
    Button prev, next;
    TextView cross;
    private int indexOfImageFile = 0;
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

        //show first gif when startActivity
        Glide.with(PopupForImage.this).asGif().load(AllConstans.SERVER_BASE_GIF_IMAGE_URL + cnchars.get(indexOfImageFile)+".gif").into(imageView);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexOfImageFile -=1;
                next.setVisibility(View.VISIBLE);
                if(indexOfImageFile==0){
                    prev.setVisibility(View.GONE);
                }
                if(indexOfImageFile>=0){
                    Glide.with(PopupForImage.this).asGif().load(AllConstans.SERVER_BASE_GIF_IMAGE_URL + cnchars.get(indexOfImageFile)+".gif").into(imageView);
                }else {
                    indexOfImageFile = 0;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexOfImageFile+=1;
                prev.setVisibility(View.VISIBLE);
                if(indexOfImageFile == cnchars.size()-1)
                {
                    next.setVisibility(View.GONE);
                }
                if(indexOfImageFile<cnchars.size()){
                    Glide.with(PopupForImage.this).asGif().load(AllConstans.SERVER_BASE_GIF_IMAGE_URL + cnchars.get(indexOfImageFile)+".gif").into(imageView);
                }else {
                    indexOfImageFile = cnchars.size()-1;
                }
            }
        });
    }
}
