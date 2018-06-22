package com.example.dkalev.remember.flashcard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.model.CardViewModelKT;
import com.example.dkalev.remember.model.Injection;
import com.example.dkalev.remember.model.ViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class EditCardActivity extends AppCompatActivity {


    private static final String DEBUG_TAG = "EditCardActivity";
    private CardViewModelKT mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    @BindView(R.id.cardFrontEditText) EditText frontET;
    @BindView(R.id.cardBackEditText) EditText backET;
    @BindView(R.id.card_edit_view_front) CardView front;
    @BindView(R.id.card_edit_view_back) CardView back;
    private int mCardSide;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        ButterKnife.bind(this);

        ViewModelFactory vmf = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, vmf).get(CardViewModelKT.class);

        Intent intent = getIntent();
        int card_uid = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_UID, 0);
        mCardSide = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_SIDE, 0);

        Log.d(DEBUG_TAG, "card_uid: " + card_uid);
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

    private void updateTextFront(final String textFront){
        mDisposable.add(mViewModel.setTextFront(textFront)
                .subscribe(()->{
                    Toast.makeText(
                            getApplicationContext(),
                            "Updated front with: "+ textFront,
                            Toast.LENGTH_LONG).show();
                },
                throwable -> Log.e(DEBUG_TAG, "Unable to update text front", throwable)));
    }

    private void updateTextBack(final String textBack){
        mDisposable.add(mViewModel.setTextBack(textBack)
                .subscribe(()->{
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Updated back with: "+ textBack,
                                    Toast.LENGTH_LONG).show();
                        },
                        throwable -> Log.e(DEBUG_TAG, "Unable to update text back", throwable)));
    }

    private void getCard(int card_uid){
        Log.d(DEBUG_TAG, "card uid: "+card_uid);
        mDisposable.add(mViewModel.getTextFront(card_uid)
        .subscribe(textFront -> {
                    Toast.makeText(
                            getApplicationContext(),
                            "Got front: "+ textFront,
                            Toast.LENGTH_LONG).show();
                    frontET.setText(textFront);},
                throwable -> Log.e(DEBUG_TAG, "Unable to retrieve card", throwable)));

        mDisposable.add(mViewModel.getTextBack(card_uid)
        .subscribe(textBack -> backET.setText(textBack)));
    }

    private class TextEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(DEBUG_TAG, "action id " + actionId + ", key event " + event);
//            if (actionId == EditorInfo.IME_NULL
//                    && event.getAction() == KeyEvent.ACTION_DOWN) {
//                example_confirm();//match this behavior to your 'Send' (or Confirm) button
//            }
//            return true;
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    if (mCardSide == 0) {
                        updateTextFront(v.getText().toString());
                    }else{
                        updateTextBack(v.getText().toString());
                    }
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }
}
