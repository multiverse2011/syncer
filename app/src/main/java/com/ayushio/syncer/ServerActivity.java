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

public class ServerActivity extends Activity implements UDPserver{

    private Button bt;
    private TextView tv;
    private int count;
    UDP udp;
    private MyThread mThread;
    private MyHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        mHandler = new MyHandler();

        Intent intent = this.getIntent();

        tv = (TextView)findViewById(R.id.textView1);
        bt = (Button)findViewById(R.id.button1);
        //bt.setOnClickListener(this);
        udp = new UDP(this);
        new MyThread().start();
    }


    class MyThread extends Thread{
        public void run(){
            udp = new UDP();
            udp.boot(50000);
        }
    }

    class MyHandler extends Handler {
        public void hanleMessage(Message msg){
            String text = msg.obj.toString();
            Log.d("called", "msg");
            tv.setText(text);
        }
    }

    interface UDPserver {
        public void recv(String host, int port, String data);
    }

    @Override
    public void recv(String host, int port, String data) {
        // TODO Auto-generated method stub

        String text = "data: " + data + " " + host + " ["+ port + "]" + "\n";
        Message msg = Message.obtain();
        msg.what=1;
        msg.obj = new String(text);
        mHandler.sendMessage(msg);

        new MyThread().start();
    }
}