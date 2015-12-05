
/*
package org.bairdmich.soundcontrol;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_entry);
        TextView list = (TextView) findViewById(R.id.audioAppList);
        list.setAdapter(new Adapter());
    }

    public class Adapter extends BaseAdapter {
        LayoutInflater layoutInflter;

        public Adapter() {
            layoutInflter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView label;
            SeekBar seek;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflter.inflate(R.layout.list_entry, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.label = (TextView) convertView
                        .findViewById(R.id.applicationName);
                viewHolder.seek = (SeekBar) convertView
                        .findViewById(R.id.volumeControlSeek);
                convertView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.seek
                    .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            ((TextView) findViewById(R.id.applicationName))
                                    .setText("onStop");
                            Log.d("seekBarInsideTheList", "onStop");
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            ((TextView) findViewById(R.id.applicationName))
                                    .setText("onStart");
                            Log.d("seekBarInsideTheList", "onStart");
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar,
                                                      int progress, boolean fromUser) {
                            ((TextView) findViewById(R.id.applicationName))
                                    .setText("onChange");
                            Log.d("seekBarInsideTheList", "onChange");
                        }
                    });

            return convertView;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Integer getItem(int arg0) {
            return arg0;
        }

    }

}
*/



/*
public class MyListActivity extends Activity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2"};
        // use your custom layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_entry, R.id.applicationName, values);
        setListAdapter(adapter);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }


}*/
