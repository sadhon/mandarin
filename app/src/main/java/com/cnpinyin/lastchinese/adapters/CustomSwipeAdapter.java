package com.cnpinyin.lastchinese.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.extras.PageContent;

import java.util.ArrayList;

/**
 * Created by User on 10/26/2017.
 */

public class CustomSwipeAdapter extends PagerAdapter {

    Context ctx;
    private int size ;
    ArrayList<PageContent> contents;


    public CustomSwipeAdapter(Context ctx, int size, ArrayList<PageContent> contents) {

        this.ctx = ctx;
        this.size = size;
        this.contents = contents;

/*
        Toast.makeText(ctx, "Hello from heere and size : " + size, Toast.LENGTH_SHORT).show();
*/
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object );
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.page_recycler_view, container, false);

        RecyclerView recyclerView = (RecyclerView) item_view.findViewById(R.id.page_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);

        PageRecyclerViewAdapter pageRecyclerViewAdapter = new PageRecyclerViewAdapter(contents);
        recyclerView.setAdapter(pageRecyclerViewAdapter);

        container.addView(item_view);


        return item_view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
