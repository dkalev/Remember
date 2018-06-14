package com.example.dkalev.remember.deck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dkalev.remember.R;

public class CreateDeckActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.createdeckview.REPLY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);

        EditText deckNameTextView = findViewById(R.id.deckNameTextView);
        Button createDeckButton = findViewById(R.id.newDeckButton);

        createDeckButton.setOnClickListener(v -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(deckNameTextView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String deckName = deckNameTextView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, deckName);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
