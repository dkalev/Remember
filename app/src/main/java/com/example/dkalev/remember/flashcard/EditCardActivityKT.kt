package com.example.dkalev.remember.flashcard

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.example.dkalev.remember.R
import com.example.dkalev.remember.model.CardViewModelKT
import com.example.dkalev.remember.model.Injection
import io.reactivex.disposables.CompositeDisposable



class EditCardActivityKT : AppCompatActivity() {
    private var mViewModel: CardViewModelKT? = null
    private val mDisposable = CompositeDisposable()
    @BindView(R.id.cardFrontEditText)
    internal var frontET: EditText? = null
    @BindView(R.id.cardBackEditText)
    internal var backET: EditText? = null
    @BindView(R.id.card_edit_view_front)
    internal var front: CardView? = null
    @BindView(R.id.card_edit_view_back)
    internal var back: CardView? = null
    private var mCardSide: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)
        ButterKnife.bind(this)

        val vmf = Injection.provideViewModelFactory(this)
        mViewModel = ViewModelProviders.of(this, vmf).get(CardViewModelKT::class.java!!)

        val intent = intent
        val card_uid = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_UID, 0)
        mCardSide = intent.getIntExtra(CardFlipActivity.EXTRA_CARD_SIDE, 0)

        Log.d(DEBUG_TAG, "card_uid: $card_uid")
        //editor action listener does not work otherwise
        frontET!!.maxLines = 1
        frontET!!.inputType = InputType.TYPE_CLASS_TEXT
        backET!!.maxLines = 1
        backET!!.inputType = InputType.TYPE_CLASS_TEXT

        if (mCardSide == 1) {
            front!!.visibility = View.GONE
            back!!.visibility = View.VISIBLE
            backET!!.setOnEditorActionListener(TextEditorActionListener())
            backET!!.onFocusChangeListener = TextOnFocusChangeListener()
        } else {
            frontET!!.setOnEditorActionListener(TextEditorActionListener())
            frontET!!.onFocusChangeListener = TextOnFocusChangeListener()

        }

        getCard(card_uid)

        //show hide keyboards
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        //        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private fun updateTextFront(textFront: String) {
        mDisposable.add(mViewModel!!.setTextFront(textFront)
//                .autoDisposable()
                .subscribe({
                    Toast.makeText(
                            applicationContext,
                            "Updated front with: $textFront",
                            Toast.LENGTH_LONG).show()
                }
                ) { throwable -> Log.e(DEBUG_TAG, "Unable to update text front", throwable) })
    }

    private fun updateTextBack(textBack: String) {
        mDisposable.add(mViewModel!!.setTextBack(textBack)
                .subscribe({
                    Toast.makeText(
                            applicationContext,
                            "Updated back with: $textBack",
                            Toast.LENGTH_LONG).show()
                }
                ) { throwable -> Log.e(DEBUG_TAG, "Unable to update text back", throwable) })
    }

    private fun getCard(card_uid: Int) {
        Log.d(DEBUG_TAG, "card uid: $card_uid")
        mDisposable.add(mViewModel!!.getTextFront(card_uid)
                .subscribe({ textFront ->
                    Toast.makeText(
                            applicationContext,
                            "Got front: $textFront",
                            Toast.LENGTH_LONG).show()
                    frontET!!.setText(textFront)
                }
                ) { throwable -> Log.e(DEBUG_TAG, "Unable to retrieve card", throwable) })

        mDisposable.add(mViewModel!!.getTextBack(card_uid)
                .subscribe { textBack -> backET!!.setText(textBack) })
    }

    private inner class TextEditorActionListener : TextView.OnEditorActionListener {

        override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
            Log.d(DEBUG_TAG, "action id $actionId, key event $event")
            //            if (actionId == EditorInfo.IME_NULL
            //                    && event.getAction() == KeyEvent.ACTION_DOWN) {
            //                example_confirm();//match this behavior to your 'Send' (or Confirm) button
            //            }
            //            return true;
            if ((actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event != null &&
                            event!!.action == KeyEvent.ACTION_DOWN &&
                            event!!.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event!!.isShiftPressed) {
                    // the user is done typing.
                    if (mCardSide == 0) {
                        updateTextFront(v.text.toString())
                    } else {
                        updateTextBack(v.text.toString())
                    }
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                    return true // consume.
                }
            }
            return false // pass on to other listeners.
        }
    }

    private inner class TextOnFocusChangeListener : View.OnFocusChangeListener {

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            if (!hasFocus) {

            }
        }
    }

    override fun onStop() {
        super.onStop()
        mDisposable.clear()
    }

    companion object {


        private val DEBUG_TAG = "EditCardActivity"
    }
}