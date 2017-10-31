package com.cnpinyin.lastchinese.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.extras.PageContent;

import java.util.ArrayList;

/**
 * Created by User on 10/29/2017.
 */

public class PageRecyclerViewAdapter extends RecyclerView.Adapter<PageRecyclerViewAdapter.PageViewHolder> {

    ArrayList<PageContent> pageContents;

    public PageRecyclerViewAdapter(ArrayList<PageContent> pageContents){
        this.pageContents = pageContents;
    }


    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_card_view, parent, false);
        PageViewHolder pageViewHolder = new PageViewHolder(view);
        return pageViewHolder;
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {

        PageContent content = pageContents.get(position);

        holder.page_text.setText(content.getCnchar());
        holder.translation.setText(content.getEngword());
        holder.pinyin.setText(content.getCnpinyin());


      //  Log.e("Position", position + " ");

    }

    @Override
    public int getItemCount() {
       // Log.e("size", pageContents.size()+ "");
        return pageContents.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder{
        TextView page_text, translation, pinyin;

        public PageViewHolder(View itemView) {
            super(itemView);
            page_text = (TextView) itemView.findViewById(R.id.page_txt);
            pinyin = (TextView) itemView.findViewById(R.id.pinyin_txt);
            translation = (TextView) itemView.findViewById(R.id.translation);


        }
    }
}
