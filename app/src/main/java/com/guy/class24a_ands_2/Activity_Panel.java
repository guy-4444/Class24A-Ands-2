package com.guy.class24a_ands_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.util.List;

public class Activity_Panel extends AppCompatActivity {

    private MaterialButton panel_BTN_start;
    private MaterialButton panel_BTN_stop;
    private MaterialButton panel_BTN_info;
    private MaterialTextView panel_LBL_info;

    private BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String json = intent.getStringExtra(LocationService.BROADCAST_LOCATION_KEY);
            try {
                MyLoc myLoc = new Gson().fromJson(json, MyLoc.class);
                panel_LBL_info.setText("Speed: " + myLoc.getSpeed());

            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        findViews();
        panel_BTN_start.setOnClickListener(v -> start());
        panel_BTN_stop.setOnClickListener(v -> stop());
        panel_BTN_info.setOnClickListener(v -> update());

        MyReminder.startReminder(this);
    }

    private void update() {
        //boolean isServiceRunning = LocationService.isMyServiceRunning(context);

        int counter = 0;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runs = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                counter++;
            }
        }

        panel_LBL_info.setText("Counter: " + counter);
    }

    private void start() {
        MyDB.saveState(this, true);
        sendActionToService(LocationService.START_FOREGROUND_SERVICE);
    }

    private void stop() {
        //MyDB.saveState(this, false);
        sendActionToService(LocationService.STOP_FOREGROUND_SERVICE);
    }

    private void sendActionToService(String action) {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            // or
            //ContextCompat.startForegroundService(this, startIntent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(LocationService.BROADCAST_LOCATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationBroadcastReceiver);
    }

    private void findViews() {
        panel_BTN_start = findViewById(R.id.panel_BTN_start);
        panel_BTN_stop = findViewById(R.id.panel_BTN_stop);
        panel_BTN_info = findViewById(R.id.panel_BTN_info);
        panel_LBL_info = findViewById(R.id.panel_LBL_info);
    }
}