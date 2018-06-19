package com.example.dkalev.remember.deck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dkalev.remember.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateDeckActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.createdeckview.REPLY";
    @BindView(R.id.deckNameEditText) EditText deckNameET;

    @OnClick(R.id.newDeckButton)
    public void submit(){
        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(deckNameET.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            String deckName = deckNameET.getText().toString();
            replyIntent.putExtra(EXTRA_REPLY, deckName);
            setResult(RESULT_OK, replyIntent);
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);
        ButterKnife.bind(this);


    }
}
