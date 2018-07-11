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


        Button buttonStart = findViewById(R.id.start_music);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPlay();
            }
        });

        Button buttonStop = findViewById(R.id.stop_music);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null){
                    msec = mediaPlayer.getCurrentPosition();
                    audioStop();
                }
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

    private boolean audioSetup(){
        boolean fileCheck = false;

        // インタンスを生成
        mediaPlayer = new MediaPlayer();

        //音楽ファイル名, あるいはパス
        String filePath = "music.mp3";

        // assetsから mp3 ファイルを読み込み
        try(AssetFileDescriptor afdescripter = getAssets().openFd(filePath);)
        {
            // MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),
                    afdescripter.getStartOffset(),
                    afdescripter.getLength());
            // 音量調整を端末のボタンに任せる
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            fileCheck = true;
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return fileCheck;
    }
    private void audioPlay() {

        if (mediaPlayer == null) {
            // audio ファイルを読出し
            if (audioSetup()){
                Toast.makeText(getApplication(), "Read audio file", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplication(), "Error: read audio file", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else{
            // 繰り返し再生する場合
            // リソースを開放しないとメモリがパンクして死ぬ
            mediaPlayer.stop();
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
        }

        // 再生する
        mediaPlayer.seekTo(msec);
        mediaPlayer.start();

        // 終了を検知するリスナー
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("debug","end of audio");
                audioStop();
            }
        });
    }

    private void audioStop() {
        // 再生終了
        mediaPlayer.stop();
        // リセット
        mediaPlayer.reset();
        // リソースの解放
        mediaPlayer.release();

        mediaPlayer = null;
    }
}
