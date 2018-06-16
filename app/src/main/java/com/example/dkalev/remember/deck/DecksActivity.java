package com.example.dkalev.remember.deck;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.flashcard.CardFlipActivity;
import com.example.dkalev.remember.model.Card;
import com.example.dkalev.remember.model.Deck;
import com.example.dkalev.remember.model.DeckViewModel;
import com.example.dkalev.remember.model.Injection;
import com.example.dkalev.remember.model.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DecksActivity extends AppCompatActivity {

    private ViewModelFactory mViewModelFactory;

    private DeckViewModel mViewModel;

    private DecksAdapter mDecksAdapter;

    private final static String DEBUG_TAG = "DecksActivity";

    public static final String DECK_ID_EXTRA = "com.example.android.decksactivity.deckid";

    private List<Deck> mDecks;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private static final int CREATE_DECK_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecyclerView();

        //inject the viewModel / db dependencies
        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DeckViewModel.class);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
           Intent intent = new Intent(DecksActivity.this, CreateDeckActivity.class);
           startActivityForResult(intent, CREATE_DECK_ACTIVITY_REQUEST_CODE);
        });
    }


    private void setupRecyclerView(){
        mDecks = new ArrayList<>();

        mDecksAdapter = new DecksAdapter(mDecks);

        RecyclerView rv = findViewById(R.id.decksRecyclerView);

        rv.setAdapter(mDecksAdapter);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.addOnItemTouchListener(new DeckRecyclerTouchListener(this, rv, new DeckRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view) {
                //if you click not on item returns -1 and crashes
                if (rv.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
                    Log.d(DEBUG_TAG, "click");
                    Intent intent = new Intent(DecksActivity.this, CardFlipActivity.class);
                    int pos = rv.getChildAdapterPosition(view);
                    Deck deck = mDecks.get(pos);
                    intent.putExtra(DECK_ID_EXTRA, deck.getDeckId());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view) {
                Log.d(DEBUG_TAG, "long click");
                int pos = rv.getChildAdapterPosition(view);
                Deck deck = mDecks.get(pos);
                mDisposable.add(mViewModel.deleteDeck(deck)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    mDecks.remove(deck);
                                    mDecksAdapter.notifyItemRemoved(pos);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Deck deleted",
                                            Toast.LENGTH_LONG).show();},
                                throwable -> Log.e(DEBUG_TAG, "Unable to delete deck", throwable)));
            }

            @Override
            public void onFling(View view, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(DEBUG_TAG, "fling");
                Deck deck = mDecks.get(rv.getChildAdapterPosition(view));
                Card card = new Card(deck.getDeckId());
                card.setTextFront("Front");
                card.setTextBack("Back");
                mDisposable.add(mViewModel.addCard(card)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() ->
                            Log.d(DEBUG_TAG, "Card added")
                    ,throwable -> Log.e(DEBUG_TAG, "Unable to add deck", throwable)));
            }
        }));
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_DECK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Deck deck = new Deck(data.getStringExtra(CreateDeckActivity.EXTRA_REPLY));
            mDisposable.add(mViewModel.addDecks(deck)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        mDecksAdapter.notifyItemInserted(mDecksAdapter.getItemCount());
                        Toast.makeText(
                            getApplicationContext(),
                            "Added new deck",
                            Toast.LENGTH_LONG).show();},
                            throwable -> Log.e(DEBUG_TAG, "Unable to add deck", throwable)));
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Empty input field",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Subscribe to the emissions of the list of decks from the view model.
        // Update the decks recyclerview, at every onNext emission.
        // In case of error, log the exception.
        mDisposable.add(mViewModel.getAllDecks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(decks -> {
                    //will be better if update just the difference
                    mDecks.clear();
                    mDecks.addAll(decks);
                    mDecksAdapter.notifyDataSetChanged();},
                        throwable -> Log.e(DEBUG_TAG, "Unable to get decks", throwable)));
    }

    @Override
    protected void onStop() {
        super.onStop();

        // clear all the subscriptions
        mDisposable.clear();
    }


}
