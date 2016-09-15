package org.bairdmich.soundcontrol;

import android.os.NetworkOnMainThreadException;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Michael on 27/01/2015.
 */
public class ConnectSocketUDP implements Runnable {

    private static final String TAG = ConnectSocketUDP.class.toString();
    private static final int STOP_PACKET_TYPE = 1;
    private static final int UNKNOWN_PACKET_TYPE = 2;
    private static final int SUCCESS_UPDATE = 0;

    public Map<Integer, AbstractAudioSession> getList() {
        return list;
    }

    private final Map<Integer, AbstractAudioSession> list = new HashMap<>();

    private DatagramSocket socket = null;
    private InetSocketAddress socketAddress;

    private ConnectionService service;
    private final String hostname;
    private final int port;

    public ConnectSocketUDP(ConnectionService service, String hostname, int port){

        this.service = service;
        this.hostname = hostname;
        this.port = port;
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
            return UNKNOWN_PACKET_TYPE;
        }
        if (s.startsWith("Status")) {
            return processStatus(data);
        } else if (s.startsWith("Stop")) {

            return STOP_PACKET_TYPE;
        }
        Log.i(TAG, "Unknown packet type");
        return UNKNOWN_PACKET_TYPE;
    }

    private int processStatus(byte[] data) {
        Log.i(TAG, "Received status update");
        data = Arrays.copyOfRange(data, "Status".length(), data.length);
        BufferedInputStream b = new BufferedInputStream(new ByteArrayInputStream(data));

        try {
            int pid = PacketFunctions.readPid(b);
            if (pid == 999999){
                int volume = PacketFunctions.readVol(b);
                int nameLength = PacketFunctions.readLength(b);
                String name = PacketFunctions.readName(b, nameLength);
                boolean muted = PacketFunctions.readMuted(b);
                AbstractAudioSession ae = new AudioEndpoint(pid, name, volume, muted);
                list.put(pid, ae);
                Log.i(TAG, ae.toString());
            }else {
                int volume = PacketFunctions.readVol(b);
                int nameLength = PacketFunctions.readLength(b);
                String name = PacketFunctions.readName(b, nameLength);
                boolean muted = PacketFunctions.readMuted(b);
                AudioSession as = new AudioSession(pid, name, volume, muted);
                list.put(pid, as);
                Log.i(TAG, as.toString());
            }



        } catch (IOException e) {
            e.printStackTrace();
            // todo what shall we do if error? return success or not?
        }
        return SUCCESS_UPDATE;
    }

    private boolean readMuted(BufferedInputStream b) throws IOException {

        return PacketFunctions.readMuted(b);
    }



    private InetSocketAddress getINetSocketAddress(String hostname, int port) {

        Log.d(TAG, "Hostname: '" + hostname + "'");
        Log.d(TAG, "Port number: '" + port + "'");

        try {
            return new InetSocketAddress(hostname, port);
        } catch (NetworkOnMainThreadException e){
            try {
                return new InetSocketAddress(InetAddress.getByName(hostname), port);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
        }

        return null;

    }

    /**
     * tells server it is here,
     */
    private boolean initConnection(DatagramSocket s, InetSocketAddress address) {

        byte[] data = "Hello".getBytes();


        try {
            s.connect(address);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to connect to: " + address.getHostName() + " " + address.getPort() + ", SocketException");
        }
        DatagramPacket p = new DatagramPacket(data, data.length);
        try {
            s.send(p);
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


    @SuppressWarnings("BooleanParameter")
    public void update(AbstractAudioSession as) {
        byte[] update = "Update".getBytes();

        byte[] pidBytes = ByteBuffer.allocate(4).putInt(as.getPid()).array();
        byte[] volumeBytes = ByteBuffer.allocate(4).putInt(as.getVolume()).array();

        byte[] dst = new byte[update.length + pidBytes.length + volumeBytes.length + 1]; // 1 for muted
        System.arraycopy(update, 0, dst, 0, update.length);
        System.arraycopy(pidBytes, 0, dst, update.length, pidBytes.length);
        System.arraycopy(volumeBytes, 0, dst, update.length + pidBytes.length, volumeBytes.length);

        dst[dst.length - 1] = (byte) (as.isMuted() ? 1 : 0);

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

    @Override
    public void run() {
        Log.d(TAG, "doing in runnable");
        this.socketAddress = getINetSocketAddress(hostname, port);
        //String message = "uninitialised";

        try {
            socket = new DatagramSocket();

            if (initConnection(socket, socketAddress)) {
                //send successful.

                while (socket.isConnected()) {
                    DatagramPacket p = new DatagramPacket(new byte[1000], 1000);
                    socket.receive(p);

                    switch (readData(p.getData())) {
                        case SUCCESS_UPDATE:
                            // success. update ui.
                            // todo
                            service.update(list, this);
                            break;
                        case STOP_PACKET_TYPE:
                            socket.close();
                            // todo
                            service.stopService();
                            break;
                        case UNKNOWN_PACKET_TYPE:
                            //unknown outcome
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getCause());
            System.err.println("Couldn't get I/O for the connection to '" +
                    socketAddress.getHostName() + "'");
            //message = "Couldn't get I/O for the connection to '" +
            //        socketAddress.getHostName() + "'";
        } finally {
            if (socket != null) socket.close();
            Log.d(TAG, "runnable is done");
        }



    }
}
