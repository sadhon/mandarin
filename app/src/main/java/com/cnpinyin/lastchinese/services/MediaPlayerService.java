package com.cnpinyin.lastchinese.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cnpinyin.lastchinese.R;

/**
 * Created by User on 11/6/2017.
 */

public class MediaPlayerService extends Service {
    MediaPlayer mediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String url = intent.getStringExtra("url");

      //  Toast.makeText(this, "" + url, Toast.LENGTH_SHORT).show();
        Log.e("url", url);


  /*      mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(soundFile);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //loading sound playing gif
         //   Glide.with(ctx).load(R.raw.sound_playing).into(holder.sound_icon);
           // holder.sound_icon.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
       //     Toast.makeText(ctx, "" + e, Toast.LENGTH_SHORT).show();
        }*/

/*        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.sound_icon.setEnabled(true);
                holder.sound_icon.setImageResource(R.drawable.sound_icon);
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });*/


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
