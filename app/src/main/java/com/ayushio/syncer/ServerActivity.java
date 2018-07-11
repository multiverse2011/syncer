package com.ayushio.syncer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class ServerActivity extends Activity implements /*OnClickListener,*/UDPserver {

    private Button bt;
    private TextView tv;
    private MyHandler mHandler;
    private MyThread mThread;
    UDP udp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mHandler = new MyHandler();
        tv = (TextView)findViewById(R.id.textView1);
		/*rctv = (TextView)findViewById(R.id.textView2);
		bt = (Button)findViewById(R.id.button1);
		bt.setOnClickListener(this);*/

        mThread = new MyThread();
        mThread.start();				//�X���b�h��p�������N���X��NEW������K��start()����s����

    }

    class MyThread extends Thread{
        public void run(){
            Log.d("thread", "thread run");
            UDP udp = new UDP(ServerActivity.this);		//������MainActivity��UDP��R�Â���
            udp.boot(50000);

        }
    }
    @Override
    public void recv(String host, int port, String data) {
        Log.d("recv", "checkrcv");
        Message msg = Message.obtain();						//handler�Ńf�[�^��n���BsetText���ƃA�v����������
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
            int msec = Integer.parseInt(str);
            tv.setText(String.valueOf(msec));								//recv()�Ŏ󂯎����data������ĕ\��
        }
    }
}