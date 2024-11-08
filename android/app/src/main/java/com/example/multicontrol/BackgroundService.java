package com.example.multicontrol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BackgroundService extends Service {
    private static final String CHANNEL_ID = "InputServiceChannel";
    private static final String SERVER_IP = "192.168.229.86";  // Replace with your server IP
    private static final int SERVER_PORT = 8080;              // Replace with your server port
    private Socket socket;
    private DataOutputStream outputStream;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable eventSender = new Runnable() {
        @Override
        public void run() {
            sendEvent("MOUSE_MOVE", 100, 200);
            handler.postDelayed(this, 50);  // Repeat every EVENT_INTERVAL
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TCP Client", "Connecting to server");
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TCP Client Service")
                .setContentText("Sending events to remote server")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);

        new Thread(this::connectToServer).start();  // Connect to server on a new thread
    }

    private void connectToServer() {

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            outputStream = new DataOutputStream(socket.getOutputStream());
            Log.d("TCP Client", "Connected to server");

            // start sending events once connected
            handler.post(eventSender);

            Intent intent = new Intent("com.example.multicontrol.CONNECTION_ESTABLISHED");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            Log.d("TCP Client", "Sent broadcast to finish");
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    public void sendEvent(String eventType, int x, int y) {
        new Thread(() -> {
            if (outputStream != null) {
                try {
                    String message = eventType + ":" + x + "," + y;
                    outputStream.writeUTF(message);
                    outputStream.flush();
//                    Log.d("TCP Client", "Event sent: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                    stopSelf();  // Stop the service if an error occurs
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Here you can handle any action to send events from UI or BroadcastReceiver
        // Example: sendEvent("MOUSE_MOVE", 100, 200);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(eventSender);  // Stop sending events
        try {
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "TCP Client Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}

