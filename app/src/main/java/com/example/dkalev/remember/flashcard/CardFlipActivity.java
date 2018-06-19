package com.example.dkalev.remember.flashcard;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.animation.FlingAnimation;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

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
    public static final String EXTRA_CARD_UID = "EXTRA_CARD_UID";
    public static final String EXTRA_CARD_SIDE = "EXTRA_CARD_UID";

    private CardsAdapter mCardsAdapter;
    private RecyclerView mRecyclerView;

    private DeckViewModel mViewModel;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private ArrayList<Card> mCards;
    private int mDeckId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        ViewModelFactory vmf = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, vmf).get(DeckViewModel.class);

        mDeckId = getIntent().getIntExtra(DecksActivity.DECK_ID_EXTRA, 0);

        mCards = fetchDeckCards(mDeckId);

        setupRecyclerView(mCards);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDeckCards(mDeckId);
    }

    private ArrayList<Card> fetchDeckCards(int deckId){
        ArrayList<Card> cards = new ArrayList<>();
        mDisposable.add(mViewModel.getDeck(deckId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d -> {
                            cards.addAll(d.getCards());
                            mCards.clear();
                            mCards.addAll(d.getCards());
                            mCardsAdapter.notifyDataSetChanged();
                        },
                        throwable -> Log.e(DEBUG_TAG, "Unable to fetch cards", throwable)));
        return cards;
    }

    private void setupRecyclerView(ArrayList<Card> cards){
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

        mRecyclerView.setItemAnimator(new CardItemAnimator());

        mRecyclerView.addOnItemTouchListener(new CardRecyclerTouchListener(this, mRecyclerView, new CardRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(FlashcardView view) {
                Log.d(DEBUG_TAG, "click");
                view.flipCard();
            }

            @Override
            public void onLongClick(FlashcardView view) {
                Log.d(DEBUG_TAG, "longclick");
                int pos = mRecyclerView.getChildAdapterPosition(view);
                if (pos != -1) {
                    Card card = mCards.get(pos);
                    int side = view.getSide();
                    Log.d(DEBUG_TAG, "card uid:" + card.getUid());
                    startEditCardActivity(view, card.getUid(), side);
                }
            }

            @Override
            public void onFling(final FlashcardView view, MotionEvent e1, MotionEvent e2, float velocityX,
                                float velocityY) {
                Log.d(DEBUG_TAG, "fling");
                if(cards.size() > 0 && view.isFlipped()) {
                    FlingAnimation flingX = new FlingAnimation(view, FlingAnimation.TRANSLATION_X).setFriction(1.1f);
                    FlingAnimation flingY = new FlingAnimation(view, FlingAnimation.TRANSLATION_Y).setFriction(1.1f);

                    flingX.setStartVelocity(velocityX);
                    flingY.setStartVelocity(velocityY);

                    flingX.start();
                    flingY.start();
                    flingX.addEndListener((animation, canceled, value, velocity) -> {
                        cards.remove(0);
                        mRecyclerView.getAdapter().notifyItemRemoved(0);
                    });
                    //todo open next activity when out of cards
                }
            }
        }));
    }


    private void startEditCardActivity(FlashcardView view, int cardUid, int side) {
        Intent intent = new Intent(this, EditCardActivity.class);
        intent.putExtra(EXTRA_CARD_UID, cardUid);
        intent.putExtra(EXTRA_CARD_SIDE, side);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade(Fade.IN));
            getWindow().setEnterTransition(new Fade(Fade.OUT));

            Pair<View, String> cardPair;
            Pair<View, String> textPair;
            if (view.getSide() == 0) {
                cardPair = new Pair<>(view.findViewById(R.id.card_view_front),
                        getString(R.string.front_card_transition));
                textPair = new Pair<>(view.findViewById(R.id.cardFrontTextView),
                        getString(R.string.frontET_transition));
            } else {
                cardPair = new Pair<>(view.findViewById(R.id.card_view_back),
                        getString(R.string.back_card_transition));
                textPair = new Pair<>(view.findViewById(R.id.cardBackTextView),
                        getString(R.string.backET_transition));
            }

            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, cardPair, textPair);

            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }
}
