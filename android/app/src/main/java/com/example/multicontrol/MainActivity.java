package com.example.multicontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private final BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TCP Client", "Received flag to finish app");
            // Close MainActivity when the connection is established
//            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startForegroundService(serviceIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(connectionReceiver, new IntentFilter("com.example.multicontrol.CONNECTION_ESTABLISHED"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to prevent memory leaks
        unregisterReceiver(connectionReceiver);
    }
}
