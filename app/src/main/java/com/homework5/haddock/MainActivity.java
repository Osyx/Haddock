package com.homework5.haddock;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.homework5.haddock.network.DownloadCallback;
import com.homework5.haddock.network.NetworkFragment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements DownloadCallback {
    private static final String TAG = "MainActivity";
    private String URL = "http://osyx.azurewebsites.net/haddock/words.txt";
    private String WORD_ERROR = "Something went wrong during word generation.";
    private String NO_NETWORK_MSG = "No network detected, fetching locally...";
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;
    boolean toast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), "https://www.google.com");

    }

    public void fetchSwearing(View view) {
        new FetchCitation().execute();
    }

    public void changeCitation(String newMessage) {
        if(toast) {
            Toast.makeText(getApplicationContext(), NO_NETWORK_MSG, Toast.LENGTH_LONG).show();
            toast = false;
        }
        TextView textView = findViewById(R.id.citationBox);
        textView.setText(newMessage);
    }

    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }


    @Override
    public void updateFromDownload(Object result) {
        // Update your UI here based on result of download.
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                Toast.makeText(getApplicationContext(), "Progress error", Toast.LENGTH_LONG).show();
                break;
            case Progress.CONNECT_SUCCESS:
                Toast.makeText(getApplicationContext(), "Connection success", Toast.LENGTH_LONG).show();
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                Toast.makeText(getApplicationContext(), "Got input stream", Toast.LENGTH_LONG).show();
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Toast.makeText(getApplicationContext(), "Reading from input stream", Toast.LENGTH_LONG).show();
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Toast.makeText(getApplicationContext(), "Read from input stream successfully", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }


    private class FetchCitation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String citation;
            try {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
                citation = "************************************* WORD FETCHED FROM SERVER **************************************";
            } catch (Exception e) {
                citation = "Nä nu blommar asfalten och skam går på torra land, det blev något knas på skutan...";
                Log.e(TAG, WORD_ERROR);
            }
            return citation;
        }

        @Override
        protected void onPostExecute(String msg) {
            changeCitation(msg);
        }

    }

}

