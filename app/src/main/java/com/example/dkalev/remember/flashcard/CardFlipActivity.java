package com.example.dkalev.remember.flashcard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.deck.DecksActivity;
import com.example.dkalev.remember.model.Card;
import com.example.dkalev.remember.model.DeckViewModel;
import com.example.dkalev.remember.model.Injection;
import com.example.dkalev.remember.model.ViewModelFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CardFlipActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "CardFlipActivity";

    private CardsAdapter mCardsAdapter;
    private RecyclerView mRecyclerView;

    private DeckViewModel mViewModel;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        CardTestUtils ctu = new CardTestUtils(52353);
//        final ArrayList<Card> cards = ctu.getNewCards(10);
        final ArrayList<Card> cards = new ArrayList<>();

        ViewModelFactory vmf = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, vmf).get(DeckViewModel.class);

        String deckName = getIntent().getStringExtra(DecksActivity.DECK_NAME_EXTRA);
        mDisposable.add(mViewModel.getDeckCards(deckName)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(d -> {
            cards.addAll(d);
            Card exampleCard = new Card();
            exampleCard.setDeckId(deckName);
            exampleCard.setTextFront("Front");
            exampleCard.setTextBack("Back");
            cards.add(exampleCard);
            mCardsAdapter.notifyDataSetChanged();
        },
                throwable -> Log.e(DEBUG_TAG, "Unable to update username", throwable)));

        mCardsAdapter = new CardsAdapter(cards);
        mRecyclerView = findViewById(R.id.cardRecyclerView);
        mRecyclerView.setAdapter(mCardsAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        mRecyclerView.setLayoutManager(llm);

        mRecyclerView.addOnItemTouchListener(new CardRecyclerTouchListener(this, mRecyclerView, new CardRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(CardView view) {
                Log.d(DEBUG_TAG, "click");
                view.flipCard();
            }

            @Override
            public void onLongClick(CardView view) {
                Log.d(DEBUG_TAG, "longclick");
            }

            @Override
            public void onFling(final CardView view, MotionEvent e1, MotionEvent e2, float velocityX,
                                float velocityY) {
                Log.d(DEBUG_TAG, "fling");
                if(cards.size() > 0 && view.isFlipped()) {
                    FlingAnimation flingX = new FlingAnimation(view, FlingAnimation.TRANSLATION_X).setFriction(1.1f);
                    FlingAnimation flingY = new FlingAnimation(view, FlingAnimation.TRANSLATION_Y).setFriction(1.1f);

                    flingX.setStartVelocity(velocityX);
                    flingY.setStartVelocity(velocityY);

                    flingX.start();
                    flingY.start();
                    flingX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            cards.remove(0);
                            mRecyclerView.getAdapter().notifyItemRemoved(0);
                        }
                    });
//                    cards.remove(0);
//                    mRecyclerView.getAdapter().notifyItemRemoved(0);
                    //todo open next activity when out of cards
                }
            }
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }
}
