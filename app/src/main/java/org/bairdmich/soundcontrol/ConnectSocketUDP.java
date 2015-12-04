package org.bairdmich.soundcontrol;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

// todo receive icons from server.
// todo receive updates from server.
// todo have master vol (speaker static at top/bottom of page).
// todo have list so i can use it without the top of my screen.
// todo tidy up.
// todo rearrange-able list.
// todo nicer login screen.
// todo horozontal mode.
// todo 'favourites' or profiles (probably need to save based on name.)

/**
 * Created by Michael on 27/01/2015.
 */
public class ConnectSocketUDP extends AsyncTask<Object, String, String> {

    private static final String TAG = ConnectSocketUDP.class.toString();

    public Set<AudioSession> list = new TreeSet<>();

    DatagramSocket socket = null;


    @Override
    protected String doInBackground(Object[] params) {
        MainActivity mainActivity = (MainActivity) params[0];
        Log.d(TAG, "doing in background");

        String message = "uninitialised";

        InetSocketAddress socketAddress = getInetSocketAddress(params);
        try {
            socket = new DatagramSocket();

            if (initConnection(socket, socketAddress)) {
                //send successful.

                while (socket.isConnected()) {
                    DatagramPacket p = new DatagramPacket(new byte[1000], 1000);
                    socket.receive(p);

                    switch (readData(p.getData())) {
                        case 0:
                            // success. update ui.
                            mainActivity.update(list, this);
                            break;
                        case 1:
                            socket.close();
                            mainActivity.stopService();
                            break;
                        case 2:
                            //unknown outcome
                            break;
                    }

                }

            }
        } catch (IOException e) {
            System.err.println(e.getCause());
            System.err.println("Couldn't get I/O for the connection to '" +
                    socketAddress.getHostName() + "'");
            message = "Couldn't get I/O for the connection to '" +
                    socketAddress.getHostName() + "'";
        } finally {
            if (socket != null) socket.close();
        }

        return message;
    }

    private int readData(byte[] data) {

        String s = null;
        try {
            s = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (s == null) {
            Log.i(TAG, "data received converted to string was null??");
            return 2;
        }
        if (s.startsWith("Status")) {
            Log.i(TAG, "Received status update");
            data = Arrays.copyOfRange(data, "Status".length(), data.length);
            BufferedInputStream b = new BufferedInputStream(new ByteArrayInputStream(data));

            try {
                int pid = readPid(b);
                int volume = readVol(b);
                int nameLength = readLength(b);
                String name = readName(b, nameLength);
                AudioSession as = new AudioSession(pid, name, volume, false);
                list.add(as);
                Log.i(TAG, as.toString());

            } catch (IOException e) {
                e.printStackTrace();

            }
            return 0;
        } else if (s.startsWith("Stop")) {

            return 1;
        }
        Log.i(TAG, "Unknown packet type");
        return 2;
    }

    private InetSocketAddress getInetSocketAddress(Object[] params) {
        String hostname = (String) params[1];
        int portNumber = (int) params[2];

        Log.d(TAG, "Hostname: '" + hostname + "'");
        Log.d(TAG, "Port number: '" + portNumber + "'");

        return new InetSocketAddress(hostname, portNumber);
    }

    /**
     * tells server it is here,
     */
    private boolean initConnection(DatagramSocket socket, InetSocketAddress address) {

        byte[] data = "Hello".getBytes();


        try {
            socket.connect(address);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to connect to: " + address.getHostName() + " " + address.getPort() + ", SocketException");
        }
        DatagramPacket p = new DatagramPacket(data, data.length);
        try {
            socket.send(p);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to send hello, SocketException");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to send hello, IOException");
            return false;
        }
        return true;
    }


    // pid is 4 bytes
    private int readPid(BufferedInputStream in) throws IOException {
        int pid1 = in.read();
        int pid2 = in.read();
        int pid3 = in.read();
        int pid4 = in.read();

        return pid1 | pid2 << 8 | pid3 << 16 | pid4 << 24;
    }

    private int readVol(BufferedInputStream in) throws IOException {
        int netFloat1 = in.read();
        int netFloat2 = in.read();
        int netFloat3 = in.read();
        int netFloat4 = in.read();
        return (netFloat1 | netFloat2 << 8 | netFloat3 << 16 | netFloat4 << 24);
    }

    // length is a long (windows) so 4 bytes
    private int readLength(BufferedInputStream in) throws IOException {
        return in.read() | in.read() << 8 | in.read() << 16 | in.read() << 24;
    }


    private String readName(BufferedInputStream in, int length) throws IOException {
        int buf[] = new int[length];
        int j = 0;
        while ((j < (length)) && (buf[j] = in.read()) != -1) {
            j++;
        }

        char[] name = new char[length / 2]; // reading 1 byte, but char (in java) is 2 bytes,

        for (int n = 0, b = 0; n < name.length; n++, b += 2) {
            name[n] = (char) (buf[b] | buf[b + 1]);
        }

        return new String(name);
    }

    @Override
    protected void onProgressUpdate(String[] s) {

        //act.callback(s[0]);

    }


    @Override
    protected void onPostExecute(String message) {
        //act.callback((String)message);
    }

    public void update(int pid, int volume, boolean muted) {
        byte[] update = "Update".getBytes();

        byte[] pidBytes = ByteBuffer.allocate(4).putInt(pid).array();
        byte[] volumeBytes = ByteBuffer.allocate(4).putInt(volume).array();
        byte[] dst = new byte[update.length + pidBytes.length + volumeBytes.length + 1]; // 1 for muted
        System.arraycopy(update, 0, dst, 0, update.length);
        System.arraycopy(pidBytes, 0, dst, update.length, pidBytes.length);
        System.arraycopy(volumeBytes, 0, dst, update.length + pidBytes.length, volumeBytes.length);

        dst[dst.length - 1] = (byte) (muted ? 1 : 0);

        DatagramPacket packet = new DatagramPacket(dst, dst.length);

        try {
            socket.send(packet);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to send update, SocketException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to send update, IOException");
        }
    }
}
