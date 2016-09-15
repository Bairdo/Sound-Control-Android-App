package org.bairdmich.soundcontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "org.bairdmich.soundcontrol.MESSAGE";

    public static final String PREFS_NAME = "ClientSettings";

    public static final String HOST_PREFS = "hostname";
    public static final String PORT_PREFS = "port";

    public static final String HOST_EXTRA = "hostname";
    public static final String PORT_EXTRA = "port";

    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;

    public void connect(View view) {
        EditText ipText = (EditText) findViewById(R.id.edit_ip);
        String hostName = ipText.getText().toString().trim();

        EditText portText = (EditText) findViewById(R.id.edit_port);
        String port = portText.getText().toString().trim();

        Intent i = new Intent(this, ConnectionService.class);
        i.putExtra(HOST_EXTRA, hostName);
        i.putExtra(PORT_EXTRA, Integer.valueOf(port));
        startService(i);

        Intent mainact = new Intent(this, MainActivity.class);
        startActivity(mainact);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(HOST_PREFS, hostName);
        editor.putInt(PORT_PREFS, Integer.valueOf(port));
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String host = settings.getString(HOST_PREFS, "");

        int port = settings.getInt(PORT_PREFS, 27015);

        EditText hostnameText = (EditText)findViewById(R.id.edit_ip);
        if (!"".equals(hostnameText)) {
            hostnameText.setText(host);
        }
        EditText portText = (EditText)findViewById(R.id.edit_port);
        portText.setText(Integer.toString(port));

        portText.setFilters(new InputFilter[]{new InputFilterMinMax(MIN_PORT, MAX_PORT)});
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
