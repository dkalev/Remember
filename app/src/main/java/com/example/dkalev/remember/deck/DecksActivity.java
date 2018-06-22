package com.example.dkalev.remember.deck;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.dkalev.remember.model.DeckViewModelKT;
import com.example.dkalev.remember.model.Injection;
import com.example.dkalev.remember.model.ViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class DecksActivity extends AppCompatActivity {

    private ViewModelFactory mViewModelFactory;

    private DeckViewModelKT mViewModel;

    private DecksAdapter mDecksAdapter;

    private final static String DEBUG_TAG = "DecksActivity";

    public static final String DECK_ID_EXTRA = "com.example.android.decksactivity.deckid";

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private static final int CREATE_DECK_ACTIVITY_REQUEST_CODE = 1;

    @BindView(R.id.decksRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setupRecyclerView();

        //inject the viewModel / db dependencies
        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(DeckViewModelKT.class);
    }

    @OnClick(R.id.fab)
    public void createDeck(){
        Intent intent = new Intent(DecksActivity.this, CreateDeckActivity.class);
        startActivityForResult(intent, CREATE_DECK_ACTIVITY_REQUEST_CODE);
    }

    private void setupRecyclerView(){

        mDecksAdapter = new DecksAdapter();

        mRecyclerView.setAdapter(mDecksAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addOnItemTouchListener(new DeckRecyclerTouchListener(this, mRecyclerView, new DeckRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view) {
                //if you click not on item returns -1 and crashes
                if (mRecyclerView.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
                    Log.d(DEBUG_TAG, "click");
                    Intent intent = new Intent(DecksActivity.this, CardFlipActivity.class);
                    intent.putExtra(DECK_ID_EXTRA, (int) view.getTag());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view) {
                Log.d(DEBUG_TAG, "long click");
                mDisposable.add(mViewModel.deleteDeck((int) view.getTag())
                        .subscribe(() -> Toast.makeText(
                                getApplicationContext(),
                                "Deck deleted",
                                Toast.LENGTH_LONG).show(),
                                throwable -> Log.e(DEBUG_TAG, "Unable to delete deck", throwable)));
            }

            @Override
            public void onFling(View view, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(DEBUG_TAG, "fling");
                Card card = new Card((int) view.getTag());
                card.setTextFront("Front");
                card.setTextBack("Back");
                mDisposable.add(mViewModel.addCard(card)
                    .subscribe(() ->
                            Log.d(DEBUG_TAG, "Card added")
                    ,throwable -> Log.e(DEBUG_TAG, "Unable to add deck", throwable)));
            }
        }));
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_DECK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            final String deckName = data.getStringExtra(CreateDeckActivity.EXTRA_REPLY);
            mDisposable.add(mViewModel.addDeck(deckName)
                    .subscribe(() -> mDecksAdapter.notifyItemInserted(mDecksAdapter.getItemCount()),
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
                .subscribe(decks -> mDecksAdapter.setDeckList(decks),
                        throwable -> Log.e(DEBUG_TAG, "Unable to get decks", throwable)));
    }

    @Override
    protected void onStop() {
        super.onStop();

        // clear all the subscriptions
        mDisposable.clear();
    }


}
