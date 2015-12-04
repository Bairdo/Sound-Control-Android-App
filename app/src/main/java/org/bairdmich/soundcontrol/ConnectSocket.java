/*
package org.bairdmich.soundcontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

*/
/**
 * Created by Michael on 27/01/2015.
 *//*

public class ConnectSocket extends AsyncTask<Object, String ,String> {

    private static final String TAG = ConnectSocket.class.toString();

    public Set<AudioSession> list = new TreeSet<AudioSession>();

    LoginActivity act;

    @Override
    protected String doInBackground(Object[] params) {
        act = (LoginActivity) params[0];
        String message = "uninitialised";
        try {
            Socket kkSocket = null;
            PrintWriter out = null;
            BufferedInputStream in = null;

            String hostName = (String) params[1];
            int portNumber = Integer.parseInt((String)params[2]);

            Log.d(TAG, "Hostname: '" + hostName + "'");
            Log.d(TAG, "Port number: '"  + portNumber + "'");
            try {
                kkSocket = new Socket(hostName, portNumber);
                out = new PrintWriter(kkSocket.getOutputStream(), true);

               in = new BufferedInputStream(kkSocket.getInputStream());
                //in = new BufferedReader(
               //         new InputStreamReader(kkSocket.getInputStream()));

                String fromServer;
                String fromUser;
                message = "success";
                publishProgress(message);
                int i = 0;
                int type;
                while ((type = in.read()) != -1) {

                    switch (type & 0xffff) {
                        case 0x0001: { // i know the braces arent needed but then i can use length twice. although i could anyway
                            receiveVolume(in);
                            break;
                        }
                        case 0x0002: {
                            receiveName(in);
                            break;
                        }
                        case 0x0004: {
                            receiveAll(in);
                        }
                    }
                }
            } catch (UnknownHostException e) {
                System.err.println(e);
                System.err.println("Don't know about host '" + hostName + "'");
                message = "Don't know about host '" + hostName + "'";
            } catch (IOException e) {
                System.err.println(e.getCause());
                System.err.println("Couldn't get I/O for the connection to '" +
                        hostName + "'");
                message = "Couldn't get I/O for the connection to '" +
                        hostName + "'";
            } finally {
                if (kkSocket != null) kkSocket.close();
                if (out != null) out.close();
                if (in != null) in.close();
            }
        }catch (IOException e){
            System.err.println("Error when closing.\n"  + e);
        }


        return message;
    }

    private void receiveAll(BufferedInputStream in) throws IOException {
        Log.d(TAG, "receiving all info");

        int pid = readPid(in);

        int vol = readVol(in);
        Log.d(TAG, "real vol: " + vol);

        int length = readLength(in);
        Log.d(TAG, "Length of payload: " + length);


        String name = readName(in, length);

        Log.d(TAG, "real name: " + name);

        list.add(new AudioSession(pid, name, vol));
    }

    private int readPid(BufferedInputStream in) throws IOException {
        int pid1 = in.read();
        int pid2 = in.read();
        int pid3 = in.read();
        int pid4 = in.read();

        return pid1 | pid2 << 8 | pid3 << 16 | pid4 << 24;
    }

    private void receiveVolume(BufferedInputStream in) throws IOException {
        Log.d(TAG, "receiving volume");
        int length = readLength(in);
        Log.d(TAG, "Length of payload: " + length);
        float vol = readVol(in);

        Log.d(TAG, "real vol: " + vol);
        return;
    }

    private int readVol(BufferedInputStream in) throws IOException {
        int netFloat1 = in.read();
        int netFloat2 = in.read();
        int netFloat3 = in.read();
        int netFloat4 = in.read();
        return Float.intBitsToFloat(netFloat1 | netFloat2 << 8 | netFloat3 << 16 | netFloat4 << 24);
    }

    private int readLength(BufferedInputStream in) throws IOException {
        return in.read() | in.read() << 8 | in.read() << 16| in.read() << 24;
    }

    private void receiveName(BufferedInputStream in) throws IOException {
        Log.d(TAG, "receiving name");

        int length = readLength(in);
        Log.d(TAG, "Length of payload: " + length);
        String s = readName(in, length);

        Log.d(TAG, s);
        publishProgress(s);
    }

    private String readName(BufferedInputStream in, int length) throws IOException {
        int buf [] = new int[length];
        int j = 0;
        while (( j < (length)) && (buf[j] = in.read()) != -1){
            j++;
        }

        char [] name = new char[length/2]; // reading 1 byte, but char is 2 bytes,

        for (int n = 0, b = 0; n < name.length; n++, b+=2){
            name[n] = (char) (buf[b]| buf[b+1]);

        }

        return new String(name);
    }

    @Override
    protected void onProgressUpdate(String[] s){

        act.callback(s[0]);

    }


    @Override
    protected void onPostExecute(String message) {
        act.callback((String)message);
    }
}
*/
