package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cnpinyin.lastchinese.R;

import java.io.IOException;

public class NewActivity extends AppCompatActivity {

    Button button;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


               mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {

                    mediaPlayer.setDataSource("http://docs.google.com/uc?export=download&id=0B-5aiy1XCcO7SVl0aDhEVGJBSms");

                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(NewActivity.this, "Playback starts now", Toast.LENGTH_SHORT).show();

                    button.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        button.setEnabled(true);
                        mediaPlayer.release();
                        mediaPlayer = null;
                        Toast.makeText(NewActivity.this, "" + "playback finshied", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });






/*


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewActivity.this, DialogueImageSlider.class);
                intent.putExtra("cnchar", "你好不好钢琴谱");
                startActivity(intent);
            }
        })*/;
    }
}
