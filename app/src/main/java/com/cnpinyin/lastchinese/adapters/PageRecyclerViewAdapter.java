package com.cnpinyin.lastchinese.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.activities.DialogueImageSlider;
import com.cnpinyin.lastchinese.extras.PageContent;

import java.util.ArrayList;

/**
 * Created by User on 10/29/2017.
 */

public class PageRecyclerViewAdapter extends RecyclerView.Adapter<PageRecyclerViewAdapter.PageViewHolder> {

    private ArrayList<PageContent> pageContents;
    private Context ctx;

    public PageRecyclerViewAdapter(Context ctx,ArrayList<PageContent> pageContents){
        this.pageContents = pageContents;
        this.ctx = ctx;
    }


    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_card_view, parent, false);

        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PageViewHolder holder, int position) {

        final PageContent content = pageContents.get(position);

        holder.page_text.setText(content.getCnchar());
        holder.translation.setText(content.getEngword());
        holder.pinyin.setText(content.getCnpinyin());
        holder.writing_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DialogueImageSlider.class);
                intent.putExtra("cnchar", content.getCnchar());

                ctx.startActivity(intent);
            }
        });


      //  Log.e("Position", position + " ");

    }

    @Override
    public int getItemCount() {
       // Log.e("size", pageContents.size()+ "");
        return pageContents.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder{
        TextView page_text, translation, pinyin;
        ImageView writing_icon, sound_icon;

        public PageViewHolder(View itemView) {
            super(itemView);
            page_text = (TextView) itemView.findViewById(R.id.page_txt);
            pinyin = (TextView) itemView.findViewById(R.id.pinyin_txt);
            translation = (TextView) itemView.findViewById(R.id.translation);
            writing_icon = (ImageView) itemView.findViewById(R.id.writing_icon);
            sound_icon = (ImageView) itemView.findViewById(R.id.sound_icon);


        }
    }
}
