package com.adamdoq.fcconvert;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class DownloadTask extends AsyncTask<String, Void, String> {
    String json = "";

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while(data != -1) {
                char current = (char) data;
                json += current;
                data = reader.read();
            }
            return json;
        } catch (MalformedURLException e) {
            Log.i("error", "json failed");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("error", "json failed");
            e.printStackTrace();
        }
        return null;
    }
}
