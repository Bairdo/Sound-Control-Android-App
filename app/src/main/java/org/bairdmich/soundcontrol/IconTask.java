package org.bairdmich.soundcontrol;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Michael on 6/12/2015.
 */
public class IconTask extends AsyncTask<Integer, String, Integer> {

    public static final String TAG = IconTask.class.toString();

    private ConcurrentLinkedQueue<Integer> toget = new ConcurrentLinkedQueue<>();


    private Context context;
    public IconTask (Context context){
        context = context;
    }


    @Override
    protected Integer doInBackground(Integer... arg) {
        // setup tcp connection
        toget.addAll(Arrays.asList(arg));
        int totalSize = 0;
        try {
            downloadIcons(totalSize);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return 0;
    }

    private void downloadIcons(int totalSize) throws IOException {
        // todo remove harcoding
        String hostname = "192.168.1.69";
        int port = 27015;

        Socket s = new Socket(hostname, port);

        InputStream in = s.getInputStream();

        OutputStream outSock = s.getOutputStream();
        for (Integer i : toget) {
            totalSize++;
            //send pid

            outSock.write(("this is a tcp connection" + i.toString()).getBytes());


            int newPid = PacketFunctions.readPid(in);
            int fileNameLength = PacketFunctions.readLength(in);
            String name = PacketFunctions.readName(in, fileNameLength);
            String format = PacketFunctions.readFileType(in); // 3 bytes
            int fileLength = PacketFunctions.readLength(in);



            String outFileName = context.getCacheDir().getPath() + i.toString() + "." + format;

            OutputStream outFile = new FileOutputStream(outFileName);

            byte[] bytes = new byte[64 * 1024]; // 64 KB

            int count;
            while ((count = in.read(bytes)) < fileLength) {
                outFile.write(bytes, 0, count);
            }

            outFile.close();
            publishProgress(outFileName);
            // Escape early if cancel() is called
            if (isCancelled())
                break;


        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        //notify someone that an image is ready
    }

    @Override
    protected void onPostExecute(Integer i) {
        Log.i(TAG, "downloaded " + i + "icons");

        // might want to set a flag so no more can be added
    }

    public boolean addTask(Integer i){
        // todo if we are done dont allow add.

        return toget.add(i);
    }
}