package com.cnpinyin.lastchinese.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.activities.DialogueImageSlider;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.extras.PageContent;

import java.util.ArrayList;

/**
 * Created by User on 11/5/2017.
 */

public class Topic3PageRecyclerViewAdapter extends RecyclerView.Adapter<Topic3PageRecyclerViewAdapter.PageViewHolder> {

    private ArrayList<PageContent> pageContents;
    private Context ctx;
    MediaPlayer mediaPlayer;

    Topic3PageRecyclerViewAdapter(Context ctx, ArrayList<PageContent> pageContents) {
        this.pageContents = pageContents;
        this.ctx = ctx;
    }


    @Override
    public Topic3PageRecyclerViewAdapter.PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic3_page_card_view, parent, false);
        return new Topic3PageRecyclerViewAdapter.PageViewHolder(view);

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

        holder.sound_icon.setOnClickListener(new View.OnClickListener() {

            String soundFile = AllConstans.SERVER_BASE_SOUND_URL + content.getSoundfile();

            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mediaPlayer.setDataSource(soundFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    //loading sound playing gif
                    Glide.with(ctx).load(R.raw.sound_playing).into(holder.sound_icon);
                    holder.sound_icon.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "" + e, Toast.LENGTH_SHORT).show();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.sound_icon.setEnabled(true);
                        holder.sound_icon.setImageResource(R.drawable.sound_icon);
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                });
            }
        });


        //Image Loading
        Glide.with(ctx).load(AllConstans.SERVER_BASE_JEPG_IMAGE_URL + content.getCnchar() + ".jpeg").into(holder.topic_img);

    }


    @Override
    public int getItemCount() {
        return pageContents.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        TextView page_text, translation, pinyin;
        ImageView writing_icon, sound_icon, topic_img;

        public PageViewHolder(View itemView) {
            super(itemView);
            page_text = (TextView) itemView.findViewById(R.id.page_txt);
            pinyin = (TextView) itemView.findViewById(R.id.pinyin_txt);
            translation = (TextView) itemView.findViewById(R.id.translation);
            writing_icon = (ImageView) itemView.findViewById(R.id.writing_icon);
            sound_icon = (ImageView) itemView.findViewById(R.id.sound_icon);
            topic_img = (ImageView) itemView.findViewById(R.id.topic3_img);
        }
    }
}
