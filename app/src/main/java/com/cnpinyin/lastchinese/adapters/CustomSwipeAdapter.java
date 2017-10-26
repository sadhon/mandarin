package com.cnpinyin.lastchinese.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnpinyin.lastchinese.R;

/**
 * Created by User on 10/26/2017.
 */

public class CustomSwipeAdapter extends PagerAdapter {

    Context ctx;
    private int size ;


    public CustomSwipeAdapter(Context ctx, int size) {

        this.ctx = ctx;
        this.size = size;

        Toast.makeText(ctx, "Hello from heere and size : " + size, Toast.LENGTH_SHORT).show();
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
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);

        TextView t = (TextView) item_view.findViewById(R.id.swipe_text);

        t.setText("position is : " + position);

        container.addView(item_view);


        return item_view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
