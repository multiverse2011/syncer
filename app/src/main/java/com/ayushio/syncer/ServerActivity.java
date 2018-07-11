package com.ayushio.syncer;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

public class ServerActivity extends AppCompatActivity implements /*OnClickListener,*/UDPserver {

    private TextView tv;
    private MyHandler mHandler;
    private MyThread mThread;
    private MediaPlayer mediaPlayer;
    private int msec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mHandler = new MyHandler();
        tv = (TextView)findViewById(R.id.textView1);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String strIPAddess =
                ((ipAddress >> 0) & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        ((ipAddress >> 24) & 0xFF);

        tv.setText("My IP address is " + strIPAddess.toString());

//        Button buttonStart = findViewById(R.id.start_music);
//
//        buttonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                audioPlay(msec);
//            }
//        });
//
//        Button buttonStop = findViewById(R.id.stop_music);
//
//        buttonStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mediaPlayer != null){
//                    //msec = mediaPlayer.getCurrentPosition();
//                    audioStop();
//                }
//            }
//        });

        mThread = new MyThread();
        mThread.start();

    }

    class MyThread extends Thread{
        public void run(){
            Log.d("thread", "thread run");
            UDP udp = new UDP(ServerActivity.this);
            udp.boot(52000);

        }
    }
    @Override
    public void recv(String host, int port, String data) {
        Log.d("recv", "checkrcv");
        Message msg = Message.obtain();
        msg.obj = new String(data);
        mHandler.sendMessage(msg);

        mThread = new MyThread();
        mThread.start();
    }
    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.d("Success", "success!");
            String str = msg.obj.toString();
            str = str.replaceAll("\n", "");
            String[] command = str.split(",", 0);
            if (Objects.equals(command[0], "start")){
                if (mediaPlayer == null){
                    audioPlay(Integer.parseInt(command[1]));
                    tv.setText(command[1].toString() + "msecから再生中");
                }
            }else if(Objects.equals(command[0], "stop")){
                if (mediaPlayer != null){
                    audioStop();
                    tv.setText("再生を停止しました");
                }
            }
        }
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
    private void audioPlay(int sec) {

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
        mediaPlayer.seekTo(sec);
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