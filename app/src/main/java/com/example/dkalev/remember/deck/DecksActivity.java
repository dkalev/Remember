package com.example.dkalev.remember.deck;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.dkalev.remember.R;

import java.util.ArrayList;

public class DecksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rv = findViewById(R.id.decksRecyclerView);

        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Deck> decks = new ArrayList<>();
        decks.add(new Deck("First deck"));
        decks.add(new Deck("Second deck"));

        rv.setAdapter(new DecksAdapter(decks));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(DecksActivity.this, CreateDeckActivity.class));
            }
        });
    }

}
