package com.test.jnitest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import socket.UDPSocket;
import util.LocationUtil;

public class MainActivity extends AppCompatActivity {
    private UDPSocket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket = new UDPSocket(this);
        socket.startUDPSocket();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.stopUDPSocket();
        LocationUtil.getInstance( this ).removeLocationUpdatesListener();
    }
}
