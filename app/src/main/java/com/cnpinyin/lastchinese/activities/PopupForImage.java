package com.cnpinyin.lastchinese.activities;

import android.content.Context;
import android.content.Intent;
import android.media.ImageReader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.constants.AllConstans;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.http.Url;

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

        cross = (TextView) findViewById(R.id.btn_cross);
        imageView = (ImageView) findViewById(R.id.image_slider);
        prev = (Button) findViewById(R.id.btn_prev);
        next = (Button) findViewById(R.id.btn_next);

        final Intent intent = getIntent();
        final String cnchar = intent.getStringExtra("cnchar");
        for (int i = 0; i < cnchar.length(); i++) {
            cnchars.add(cnchar.substring(i, i + 1));
        }


        if (cnchars.size() == 1) {
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        } else {
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
        showImage(indexOfImageFile);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexOfImageFile -= 1;
                next.setVisibility(View.VISIBLE);
                if (indexOfImageFile == 0) {
                    prev.setVisibility(View.GONE);
                }
                if (indexOfImageFile >= 0) {
                    showImage(indexOfImageFile);
                } else {
                    indexOfImageFile = 0;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexOfImageFile += 1;
                prev.setVisibility(View.VISIBLE);
                if (indexOfImageFile == cnchars.size() - 1) {
                    next.setVisibility(View.GONE);
                }
                if (indexOfImageFile < cnchars.size()) {
                    showImage(indexOfImageFile);
                } else {
                    indexOfImageFile = cnchars.size() - 1;
                }
            }
        });
    }

    private void showImage(int indexOfImageFile) {
        String fileName = cnchars.get(indexOfImageFile);
        String cleanName = "";
        try {
            cleanName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String genUrl = AllConstans.SERVER_BASE_GIF_IMAGE_URL + cleanName + ".gif";

        //Image Loading by Glide
        MyTask task = new MyTask();
        task.execute(genUrl);

        Glide.with(PopupForImage.this)
                .asGif()
                .load(genUrl)
                .thumbnail(Glide.with(PopupForImage.this).asGif().load(R.raw.image_loading))
                .into(imageView);
    }


    class MyTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (!bResponse) {
                imageView.setImageResource(R.drawable.not_found);
            }
        }
    }
}
