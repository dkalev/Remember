package com.example.dkalev.remember.flashcard;

import android.os.Bundle;
import android.support.animation.FlingAnimation;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MotionEvent;

import com.example.dkalev.remember.R;

import java.util.ArrayList;

public class CardFlipActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "CardFlipActivity";

    private CardsAdapter mCardsAdapter;
    private RecyclerView mRecyclerView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        CardTestUtils ctu = new CardTestUtils(52353);
        final ArrayList<Card> cards = ctu.getNewCards(10);
        mCardsAdapter = new CardsAdapter(cards);

        mRecyclerView = findViewById(R.id.cardRecyclerView);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mCardsAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(llm);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
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
            public void onFling(CardView view, MotionEvent e1, MotionEvent e2, float velocityX,
                                float velocityY) {
                Log.d(DEBUG_TAG, "fling");
                if(cards.size() > 0 && view.isFlipped()) {
                    FlingAnimation flingX = new FlingAnimation(view, FlingAnimation.TRANSLATION_X).setFriction(1.1f);
                    FlingAnimation flingY = new FlingAnimation(view, FlingAnimation.TRANSLATION_Y).setFriction(1.1f);

                    flingX.setStartVelocity(velocityX);
                    flingY.setStartVelocity(velocityY);

                    flingX.start();
                    flingY.start();

                    cards.remove(0);
                    mRecyclerView.getAdapter().notifyItemRemoved(0);

                }
            }
        }));





//

    }
}
