package org.bairdmich.soundcontrol;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectionService extends Service {
    private ServiceHandler mServiceHandler;
    private static final String TAG = ConnectionService.class.toString();
    private final IBinder mBinder = new LocalBinder();

    private List<ConnectionActivity> activities;
    private ConnectSocketUDP server;

    public void stopService() {
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //todo????

            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long endTime = System.currentTimeMillis() + 5 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        activities = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;

//        mServiceHandler.sendMessage(msg);


        if (intent != null) {
            Bundle extras = intent.getExtras();

            if (extras == null) {
                Log.d("connectionService", "extras was null");

            } else {
                int port = extras.getInt("port");
                String hostname = extras.getString("hostname");

                Log.d("connectionService", "port: " + port);
                Log.d("connectionService", "hostname: " + hostname);

                Intent i = new Intent();
                i.setClass(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("hostname", hostname);
                i.putExtra("port", Integer.valueOf(port));
                startActivity(i);
                server = new ConnectSocketUDP(this ,hostname, port);
                mServiceHandler.post(server);
            }
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    public void update(Map<Integer, AudioSession> list, ConnectSocketUDP server){
        for (ConnectionActivity act: activities){
            act.update(list, server);
        }
    }

    public ConnectSocketUDP getServer(){
        return server;
    }

    public boolean addNotify(ConnectionActivity act){
        return activities.add(act);
    }

    public boolean removeNotify(ConnectionActivity act){
        return activities.remove(act);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        ConnectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ConnectionService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, getClass().getName() + " finished");
        Toast.makeText(this, "Connection service done.", Toast.LENGTH_SHORT).show();
    }
}