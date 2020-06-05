/*
 * /////////////////////////////////////////////////////////////////////////////////////////////////
 * //
 * //            © Copyright 2019 JangleTech Systems Private Limited, Thane, India
 * //
 * /////////////////////////////////////////////////////////////////////////////////////////////////
 */

package com.jangletech.qoogol.service;

import android.os.AsyncTask;
import android.util.Log;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

    private File sdCardRoot;
    private int position;
    private AsyncResponse callBack;

    public DownloadAsyncTask(File sdCardRoot) {
        this.sdCardRoot = sdCardRoot;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // pDialog.setProgress(values[0]);
    }

    @Override
    protected String doInBackground(String... strings) {
        String[] filesToDownload=strings[0].split(",");
        for(int i=0;i<filesToDownload.length;i++) {
            downloadFile(filesToDownload[i],(i+1)/filesToDownload.length);
        }
        if (strings[1].equalsIgnoreCase("1")) {
            return strings[1];
        }
        else {
            return null;
        }
    }


    @Nullable
    private void downloadFile(String urlfileName,float number) {
        HttpURLConnection urlConnection = null;
        String fileName;
        File file = null;
        try {
            URL url = new URL(urlfileName);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            fileName = urlfileName.substring(urlfileName.lastIndexOf('/') + 1);
            file = new File(sdCardRoot, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream inputStream = urlConnection.getInputStream();
            long totalSize = urlConnection.getContentLength();
            FileOutputStream outPut = new FileOutputStream(file);
            long downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                outPut.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                if(isCancelled()){
                    file.delete();
                    break;
                }else{
                    publishProgress((int) ((int) (Math.abs(downloadedSize * 100 / totalSize))*number));
                }
            }
            outPut.close();
            inputStream.close();
        }
        catch (IOException e) {
            Log.e("checkException:-", "" + e);
            if (file != null && file.exists()) {
                if (file.delete()) {
                    Log.e("checkException:-", "file deleted");
                }
            }
            // pDialog.dismiss();
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        // pDialog.dismiss();
    }

    public interface AsyncResponse {
        void processFinish(Object output, int position);
    }
}