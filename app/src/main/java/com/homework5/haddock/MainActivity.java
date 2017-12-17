package com.homework5.haddock;

import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.homework5.haddock.network.DownloadCallback;
import com.homework5.haddock.network.NetworkFragment;

public class MainActivity extends AppCompatActivity implements DownloadCallback, HostnameDialog.DialogListener {
    private static final String TAG = "MainActivity";
    private String HOST_URL;
    private String PLACEHOLDER_HOST = "http://192.168.10.218:8080";
    private String WORD_ERROR = "Something went wrong during word generation.";
    private String NO_NETWORK_MSG = "No network detected, fetching locally...";
    private NetworkFragment mNetworkFragment = null;
    private boolean mDownloading = false;
    boolean toast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DialogFragment dialogFragment = new HostnameDialog();
        dialogFragment.show(getFragmentManager(), "hostnameDialog");
    }

    public void fetchSwearing(View view) {
        NetworkInfo networkInfo = getActiveNetworkInfo();
        if(networkInfo != null)
            startDownload();
        else {
            toast = true;
            updateFromDownload(null);
        }
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
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }


    @Override
    public void updateFromDownload(Object result) {
        String citation = (String) result;
        if(citation == null) {
            citation = "Nä nu blommar asfalten och skam går på torra land, det blev något knas på skutan...";
            Log.e(TAG, WORD_ERROR);
        }
        changeCitation(citation);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        Toast.makeText(getApplicationContext(), percentComplete + "%", Toast.LENGTH_SHORT).show();
        switch(progressCode) {
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

    @Override
    public void onDialogPositiveClick(String hostAddress) {
        HOST_URL = "http://" + hostAddress + ":8080";
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), HOST_URL);
    }

    @Override
    public void onDialogNegativeClick() {
        HOST_URL = PLACEHOLDER_HOST;
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), HOST_URL);
    }
}

