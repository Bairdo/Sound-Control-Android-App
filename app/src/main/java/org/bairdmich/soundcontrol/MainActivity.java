package org.bairdmich.soundcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Set;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.toString();

    ConnectSocketUDP server = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main);

        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new ListAdapter(MainActivity.this));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(TAG, "extras was null");

        } else {
            int port = extras.getInt("port");
            String hostname = extras.getString("hostname");
            Log.d(TAG, "port: " + port);
            Log.d(TAG, "hostname: " + hostname);
            new ConnectSocketUDP().execute(this, hostname, port);
        }
    }

    public void update(Set<AudioSession> audioSessions, ConnectSocketUDP server) {
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
        private Context con;
        private AudioSession sessions[] = new AudioSession[0];

        public ListAdapter(MainActivity mainActivity) {
            this.con = mainActivity;
        }

        public void update(Set<AudioSession> audioSessions) {
            Log.i(TAG, "GUI is being told to update");
            sessions = audioSessions.toArray(sessions);
            Arrays.sort(sessions, new Comparator<AudioSession>() {
                @Override
                public int compare(AudioSession lhs, AudioSession rhs) {
                    return lhs.getPid() < rhs.getPid() ? 1 : lhs.getPid() == rhs.getPid() ? 0 : -1;
                }
            });
            //assert sessions.length == audioSessions.size();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return sessions.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(this.con);
            View View = inflater.inflate(R.layout.list_entry, null);

            ImageView icon = (ImageView) View.findViewById(R.id.icon);
            if (icon != null) {
                icon.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Icon in list was Clicked");

                        sessions[position].setMuted(!sessions[position].isMuted());
                        server.update(sessions[position].getPid(), sessions[position].getVolume(), sessions[position].isMuted());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                });

            }


            SeekBar seekbar = (SeekBar) View.findViewById(R.id.volumeControlSeek);
            if (seekbar != null) {
                final TextView volLevel = (TextView) View.findViewById(R.id.volumeLevel);
                volLevel.setText(String.valueOf(sessions[position].getVolume()));

                seekbar.setProgress(sessions[position].getVolume());
                final TextView appName = (TextView) View.findViewById(R.id.applicationName);
                appName.setText(sessions[position].getName());

                if (sessions[position].isMuted()) {
                    Drawable d = getResources().getDrawable(R.drawable.volumebarmuted);
                    seekbar.setProgressDrawable(d);

                } else {
                    Log.i(TAG, "getting drawable volumebar");
                    Drawable d = getResources().getDrawable(R.drawable.volumebar);
                    Log.i(TAG, "got drawable volumebar");
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
                        // todo send update to server

                        if (server != null) {
                            server.update(sessions[position].getPid(), progress, sessions[position].isMuted());
                        }

                    }
                });


            }
            return View;
        }

    }
}