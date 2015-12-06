package org.bairdmich.soundcontrol;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class MainActivity extends Activity implements ConnectionActivity {
    private static final String TAG = MainActivity.class.toString();

    private ConnectSocketUDP server = null;

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Log.i(TAG, "Volume up");
                volumeButtonPressed(event);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Log.i(TAG, "Volume down");
                volumeButtonPressed(event);
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void volumeButtonPressed(KeyEvent event) {
        ListView lv = (ListView) findViewById(R.id.list);
        ListAdapter la = (ListAdapter) lv.getAdapter();
        la.volumeButtonUpdate(event);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);

        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new ListAdapter(MainActivity.this));

        boolean ret = getApplicationContext().bindService(new Intent(this, ConnectionService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    ConnectionService mService;
    boolean mBound = false;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to ConnectionService, cast the IBinder and get ConnectionService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.addNotify(MainActivity.this);
            server = mService.getServer();

            if (server != null) {
                update(server.getList(), server);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService.removeNotify(MainActivity.this);
            mBound = false;
        }
    };

    private void updateList(){
        ListView lv = (ListView) findViewById(R.id.list);
        ListAdapter la = (ListAdapter) lv.getAdapter();
        la.update();
    }

    public void update(Map<Integer, AudioSession> audioSessions, ConnectSocketUDP server) {
        ListView lv = (ListView) findViewById(R.id.list);
        ListAdapter la = (ListAdapter) lv.getAdapter();
        la.update(audioSessions);
        this.server = server;
    }

    public void stopService() {

        Intent intent = new Intent(MainActivity.this, HelloService.class);
        stopService(intent);
    }

    private class ListAdapter extends BaseAdapter {
        private final Context con;
        private AudioSession sessions[] = new AudioSession[0];

        public ListAdapter(MainActivity mainActivity) {
            this.con = mainActivity;
        }

        public void volumeButtonUpdate(KeyEvent event) {

            for (AudioSession as : sessions) {
                if ("Speakers".equals(as.getName())) {
                    int keyCode = event.getKeyCode();
                    int step = 1;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_VOLUME_UP:
                            as.setVolume(as.getVolume() + step);
                        case KeyEvent.KEYCODE_VOLUME_DOWN:
                            as.setVolume(as.getVolume() - step);
                            break;
                    }

                    if (server != null) {
                        server.update(as.getPid(), as.getVolume(), as.isMuted());
                    }
                    update();
                    break;
                }
            }


        }

        public void update(Map<Integer, AudioSession> audioSessions) {
            Log.i(TAG, "GUI is being told to update");
            sessions = audioSessions.values().toArray(sessions);
            Arrays.sort(sessions, new Comparator<AudioSession>() {
                @Override
                public int compare(AudioSession lhs, AudioSession rhs) {
                    return lhs.getPid() < rhs.getPid() ? 1 : lhs.getPid() == rhs.getPid() ? 0 : -1;
                }
            });


            //assert sessions.length == audioSessions.size();
            update();
        }

        private void update() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            // Auto-generated method stub
            return sessions.length;
        }

        @Override
        public Object getItem(int position) {
            // Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(this.con);
            convertView = inflater.inflate(R.layout.list_entry, null);

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            if (icon != null) {
                icon.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Icon in list was Clicked");

                        sessions[position].setMuted(!sessions[position].isMuted());
                        server.update(sessions[position].getPid(), sessions[position].getVolume(), sessions[position].isMuted());
                        update();
                    }
                });

            }


            SeekBar seekbar = (SeekBar) convertView.findViewById(R.id.volumeControlSeek);
            if (seekbar != null) {
                final TextView volLevel = (TextView) convertView.findViewById(R.id.volumeLevel);
                volLevel.setText(String.valueOf(sessions[position].getVolume()));

                seekbar.setProgress(sessions[position].getVolume());
                final TextView appName = (TextView) convertView.findViewById(R.id.applicationName);
                appName.setText(sessions[position].getName());

                if (sessions[position].isMuted()) {
                    Drawable d = getResources().getDrawable(R.drawable.volumebarmuted);
                    seekbar.setProgressDrawable(d);

                } else {
                    Log.i(TAG, "getting drawable volumeBar");
                    Drawable d = getResources().getDrawable(R.drawable.volumebar);
                    Log.i(TAG, "got drawable volumeBar");
                    seekbar.setProgressDrawable(d);

                }

                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        volLevel.setText(String.valueOf(progress));
                        sessions[position].setVolume(progress);

                        if (server != null) {
                            server.update(sessions[position].getPid(), progress, sessions[position].isMuted());
                        }

                    }
                });


            }
            return  convertView;
        }

    }
}