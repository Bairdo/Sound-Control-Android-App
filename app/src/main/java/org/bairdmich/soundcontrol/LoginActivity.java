package org.bairdmich.soundcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "org.bairdmich.soundcontrol.MESSAGE";

    public void connect(View view) {
        EditText ipText = (EditText) findViewById(R.id.edit_ip);
        String hostName = ipText.getText().toString().trim();

        EditText portText = (EditText) findViewById(R.id.edit_port);
        String port = portText.getText().toString().trim();

        Intent i = new Intent(this, HelloService.class);
        i.putExtra("hostname", hostName);
        i.putExtra("port", new Integer(port));
        startService(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
