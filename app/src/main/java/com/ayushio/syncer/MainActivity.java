package com.ayushio.syncer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int msec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStop = findViewById(R.id.client);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //インテントの作成
                Intent intent = new Intent(getApplication(), ClientActivity.class);
                startActivity(intent);
            }
        });

        Button buttonServer = findViewById(R.id.server);
        buttonServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //インテントの作成
                Intent intent = new Intent(getApplication(), ServerActivity.class);
                startActivity(intent);
            }
        });
    }
}
