package com.example.dkalev.remember.flashcard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.model.Card;
import com.example.dkalev.remember.model.DeckViewModel;
import com.example.dkalev.remember.model.Injection;
import com.example.dkalev.remember.model.ViewModelFactory;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EditCardActivity extends AppCompatActivity {


    private static final String DEBUG_TAG = "EditCardActivity";
    private DeckViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private EditText frontET;
    private EditText backET;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        CardView front = findViewById(R.id.card_edit_view_front);
        CardView back = findViewById(R.id.card_edit_view_back);
        frontET = findViewById(R.id.cardFrontEditText);
        backET = findViewById(R.id.cardBackEditText);

        ViewModelFactory vmf = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, vmf).get(DeckViewModel.class);

        Intent intent = getIntent();
        int card_uid = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_UID, 0);
        int cardSide = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_SIDE, 0);

        if (cardSide == 1) {
            front.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        }

        getCard(card_uid);
    }

    private void getCard(int card_uid){
        mDisposable.add(mViewModel.getCard(card_uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(card -> {
                    //setText does not work unless in resume; the database returns too late
                    //might have to pass the text as extra in the intent
                    frontET.setText(card.getTextFront());
                    backET.setText(card.getTextBack());},
                throwable -> Log.e(DEBUG_TAG, "Unable to retrieve card", throwable)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }
}
