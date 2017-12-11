package com.homework5.haddock;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHandler {
    private static final String TAG = "NetworkHandler";
    private final String CONNECTION_ERROR = "Something went wrong during the connection.";

    InputStream getPageContent(String s_url) throws IOException {
        BufferedInputStream in = null;
        URL url = new URL(s_url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            in = new BufferedInputStream(urlConnection.getInputStream());
        }
        catch(Exception e) {
            Log.d(TAG, CONNECTION_ERROR);
        }
        finally {
            urlConnection.disconnect();
        }

        return in;
    }
}
