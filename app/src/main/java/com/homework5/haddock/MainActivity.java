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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String URL = "http://osyx.azurewebsites.net/haddock/words.txt";
    private String WORD_ERROR = "Something went wrong during word generation.";
    private String NO_NETWORK_MSG = "No network detected, fetching locally...";
    boolean toast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        while(true)
            Log.wtf(TAG, "looping");

    }

    private class FetchCitation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String citation;
            try {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
                WordHandler wh;
                if (networkInfo != null && networkInfo.isConnected()) {
                    wh = new WordHandler(new NetworkHandler().getPageContent(URL));
                } else {
                    wh = new WordHandler(getResources().openRawResource(R.raw.words));
                    toast = true;
                }
                citation = wh.randomWord();
            } catch (IOException e) {
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

