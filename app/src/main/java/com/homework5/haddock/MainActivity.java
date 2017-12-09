package com.homework5.haddock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fetchSwearing(View view) {
        new FetchCitation().execute();
    }

    public void changeCitation(String newMessage) {
        TextView textView = findViewById(R.id.citationBox);
        CharSequence initialShout = textView.getContext().getText(R.string.initial_shout);
        if(textView.getText().equals(initialShout))
            textView.setText(newMessage);
        else
            textView.setText(initialShout);

    }

    private class FetchCitation extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            String citation = "Snack och strunt och snack, det var inte det här vi ville ha.";
            try {
                WordHandler wh = new WordHandler();
                citation = wh.randomWord();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return citation;
        }

        @Override
        protected void onPostExecute(String msg) {
            changeCitation(msg);
        }

    }

}

