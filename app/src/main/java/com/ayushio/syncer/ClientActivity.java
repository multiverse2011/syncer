package com.ayushio.syncer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClientActivity extends AppCompatActivity {

    private EditText editIp;
    private EditText editLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        editIp = (EditText)findViewById(R.id.edit_ip);
        editLength = (EditText)findViewById(R.id.edit_length);

        Button buttonStart = findViewById(R.id.start_music);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editIp.getText().toString();
                String command = "start," + editLength.getText().toString();
                try {
                    new UDP().send(ip, 52000, command);
                }catch(Exception e){
                    Log.e("onClick", e.toString());
                }
            }
        });

        Button buttonStop = findViewById(R.id.stop_music);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editIp.getText().toString();
                String command = "stop";
                try {
                    new UDP().send(ip, 52000, command);
                }catch(Exception e){
                    Log.e("onClick", e.toString());
                }
            }
        });
    }
}
