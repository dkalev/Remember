package com.example.dkalev.remember.flashcard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.model.Card;
import com.example.dkalev.remember.model.CardViewModel;
import com.example.dkalev.remember.model.DeckViewModel;
import com.example.dkalev.remember.model.Injection;
import com.example.dkalev.remember.model.ViewModelFactory;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EditCardActivity extends AppCompatActivity {


    private static final String DEBUG_TAG = "EditCardActivity";
    private CardViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private EditText frontET;
    private EditText backET;
    private Card mCard;
    private int mCardSide;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        CardView front = findViewById(R.id.card_edit_view_front);
        CardView back = findViewById(R.id.card_edit_view_back);
        frontET = findViewById(R.id.cardFrontEditText);
        backET = findViewById(R.id.cardBackEditText);

        ViewModelFactory vmf = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, vmf).get(CardViewModel.class);

        Intent intent = getIntent();
        int card_uid = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_UID, 0);
        mCardSide = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_SIDE, 0);

        //editor action listener does not work otherwise
        frontET.setMaxLines(1);
        frontET.setInputType(InputType.TYPE_CLASS_TEXT);
        backET.setMaxLines(1);
        backET.setInputType(InputType.TYPE_CLASS_TEXT);

        if (mCardSide == 1) {
            front.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
            backET.setOnEditorActionListener(new TextEditorActionListener());
            backET.setOnFocusChangeListener(new TextOnFocusChangeListener());
        }else{
            frontET.setOnEditorActionListener(new TextEditorActionListener());
            frontET.setOnFocusChangeListener(new TextOnFocusChangeListener());

        }

        getCard(card_uid);

        //show hide keyboards
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private class TextEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(DEBUG_TAG, "action id " + actionId + ", key event " + event);
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    if (mCardSide == 0) {
                        mCard.setTextFront(v.getText().toString());
                    }else{
                        mCard.setTextBack(v.getText().toString());
                    }
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    updateCard();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }
    }

    private class TextOnFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {

            }
        }
    }

    private void updateCard(){

        mDisposable.add(mViewModel.updateCard(mCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {

                },
                        throwable -> Log.e(DEBUG_TAG, "Unable to update card", throwable)));
    }

    private void getCard(int card_uid){
        Log.d(DEBUG_TAG, "card uid: "+card_uid);
        mDisposable.add(mViewModel.getCard(card_uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(card -> {
                    //initially tried to assign the card to mCard: mCard = card
                    //very stupid, because probably after finishing the anonimous function the card is destroyed
                    mCard = new Card(card.getDeckId());
                    mCard.setTextFront(card.getTextFront());
                    mCard.setTextBack(card.getTextBack());
                    mCard.setUid(card.getUid());
                    Log.d(DEBUG_TAG, "Card front:" + card.getTextFront());
                    //setText does not work unless in onResume; the database returns too late
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
