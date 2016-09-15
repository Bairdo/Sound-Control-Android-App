package org.bairdmich.soundcontrol;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketFunctions {

    private static String [] fileTypes = {null, "jpg", "png", "ico"};

    private PacketFunctions(){}

    public static boolean readMuted(InputStream b) throws IOException {
        int muted = b.read();

        return muted != 0;
    }// pid is 4 bytes

    public static int readPid(InputStream in) throws IOException {
        int pid1 = in.read();
        int pid2 = in.read();
        int pid3 = in.read();
        int pid4 = in.read();

        return pid1 | pid2 << 8 | pid3 << 16 | pid4 << 24;
    }

    public static float readVolFloat(InputStream in) throws IOException {
        int netFloat1 = in.read();
        int netFloat2 = in.read();
        int netFloat3 = in.read();
        int netFloat4 = in.read();
        return Float.intBitsToFloat(netFloat1 | netFloat2 << 8 | netFloat3 << 16 | netFloat4 << 24);
    }

    public static int readVol(InputStream in) throws IOException {
        int netFloat1 = in.read();
        int netFloat2 = in.read();
        int netFloat3 = in.read();
        int netFloat4 = in.read();
        return (netFloat1 | netFloat2 << 8 | netFloat3 << 16 | netFloat4 << 24);
    }// length is a long (windows) so 4 bytes

    public static int readLength(InputStream in) throws IOException {
        return in.read() | in.read() << 8 | in.read() << 16 | in.read() << 24;
    }


    public static String readName(InputStream in, int length) throws IOException {
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

    /**
     *
     * @param in
     * @return may return null
     * @throws IOException
     */
    public static String readFileType(InputStream in) throws IOException{

        int fileType = in.read();
        if (fileType > fileTypes.length){
            return fileTypes[0];
        }
        return fileTypes[fileType];
    }


}