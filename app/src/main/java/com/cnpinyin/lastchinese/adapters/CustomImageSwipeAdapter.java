package com.cnpinyin.lastchinese.adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.constants.AllConstans;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by User on 11/2/2017.
 */

public class CustomImageSwipeAdapter extends PagerAdapter{



    private int[] imageResources = {R.drawable.cnpinyin_logo, R.drawable.ic_menu_camera, R.drawable.ic_menu_share};
    private Context ctx;
    private LayoutInflater layoutInflater;

    private ArrayList<String> cnchars = new ArrayList<>();

    public CustomImageSwipeAdapter(Context ctx, ArrayList<String> cnchars) {
        this.ctx = ctx;
        this.cnchars = cnchars;
    }

    @Override
    public int getCount() {
        return cnchars.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item_view = layoutInflater.inflate(R.layout.swipe_image_layout, container, false);

        ImageView imageView = (ImageView) item_view.findViewById(R.id.slider_image);


/*
        try {

            TextView textView = (TextView) item_view.findViewById(R.id.slider_text);

            textView.setText(cnchars.get(0));

            ImageView imageView = (ImageView) item_view.findViewById(R.id.slider_image);

            String s = URLEncoder.encode(cnchars.get(position), "UTF-8");

            Glide.with(ctx)
                    .load(AllConstans.SERVER_BASE_IMAGE_URL + s + ".gif")
                    .placeholder(R.drawable.cnpinyin_logo)
                    .error(R.drawable.ic_menu_camera)
                    .into(imageView);

            Log.e("url", AllConstans.SERVER_BASE_IMAGE_URL + s + ".gif");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("url", URLEncoder.encode(AllConstans.SERVER_BASE_IMAGE_URL));*/





        Log.e("url","http://cnpinyin.com/pinyin/PYT/ch-gif/all-files-1.1/"+cnchars.get(position)+".gif" );


        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
