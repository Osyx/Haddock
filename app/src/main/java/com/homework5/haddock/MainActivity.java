package com.homework5.haddock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void fetchSwearing(View view) {
        String newCitation = "Bomber och granater, du tryckte på knappen...\nHär tror du att du ska få snack och strunt och snack, nej nu går skam över torra land, skäms landkrabba!";
        changeCitation(view, newCitation);

    }

    public void changeCitation(View view, String newMessage) {
        TextView textView = findViewById(R.id.citationBox);
        CharSequence initialShout = view.getContext().getText(R.string.initial_shout);
        if(textView.getText().equals(initialShout))
            textView.setText(newMessage);
        else
            textView.setText(initialShout);

    }



}

