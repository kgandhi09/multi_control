package com.example.multicontrol;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private BackgroundService backgroundService = new BackgroundService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startForegroundService(serviceIntent);
    }

    public BackgroundService getBackgroundService() {
        return backgroundService;
    }

    public void setBackgroundService(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }
}
