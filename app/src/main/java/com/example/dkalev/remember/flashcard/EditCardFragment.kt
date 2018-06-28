package com.example.dkalev.remember.flashcard

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.dkalev.remember.R
import com.example.dkalev.remember.model.CardViewModel
import com.example.dkalev.remember.model.Injection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_edit_card.*


class EditCardFragment: Fragment() {

    private var viewModel: CardViewModel? = null
    private val disposable = CompositeDisposable()

    private var cardUid = 0
    private var cardSide = 0

    private val DEBUGTAG = "EditCardFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_edit_card, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val vmf = Injection.provideViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, vmf).get(CardViewModel::class.java)

        cardUid = arguments!!.get("cardUid") as Int
        cardSide = arguments!!.get("cardSide") as Int

        getCard(cardUid)


        cardFrontEditText!!.maxLines = 1
        cardFrontEditText!!.inputType = InputType.TYPE_CLASS_TEXT
        cardBackEditText!!.maxLines = 1
        cardBackEditText!!.inputType = InputType.TYPE_CLASS_TEXT

        if (cardSide == 1) {
            cardEditViewFront!!.visibility = View.GONE
            cardEditViewBack!!.visibility = View.VISIBLE
            cardBackEditText!!.setOnEditorActionListener(TextEditorActionListener())
            cardBackEditText!!.onFocusChangeListener = TextOnFocusChangeListener()
        } else {
            cardFrontEditText!!.setOnEditorActionListener(TextEditorActionListener())
            cardFrontEditText!!.onFocusChangeListener = TextOnFocusChangeListener()

        }
    }

    private fun getCard(card_uid: Int) {
        Log.d(DEBUGTAG, "card uid: $card_uid")
        disposable.add(viewModel!!.getTextFront(card_uid)
                .subscribe({ textFront ->cardFrontEditText!!.setText(textFront)}
                ) { throwable -> Log.e(DEBUGTAG, "Unable to retrieve card", throwable) })

        disposable.add(viewModel!!.getTextBack(card_uid)
                .subscribe { textBack -> cardBackEditText!!.setText(textBack) })
    }

    private fun updateTextFront(textFront: String) {
        disposable.add(viewModel!!.setTextFront(textFront)
//                .autoDisposable()
                .subscribe({
                    Toast.makeText(
                            context,
                            "Updated front with: $textFront",
                            Toast.LENGTH_LONG).show()
                }
                ) { throwable -> Log.e(DEBUGTAG, "Unable to update text front", throwable) })
    }

    private fun updateTextBack(textBack: String) {
        disposable.add(viewModel!!.setTextBack(textBack)
                .subscribe({
                    Toast.makeText(
                            context,
                            "Updated back with: $textBack",
                            Toast.LENGTH_LONG).show()
                }
                ) { throwable -> Log.e(DEBUGTAG, "Unable to update text back", throwable) })
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private inner class TextEditorActionListener : TextView.OnEditorActionListener {

        override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
            Log.d(DEBUGTAG, "action id $actionId, key event $event")
            if ((actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event != null &&
                            event!!.action == KeyEvent.ACTION_DOWN &&
                            event!!.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event!!.isShiftPressed) {
                    // the user is done typing.
                    activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                    if (cardSide == 0) {
                        updateTextFront(v.text.toString())
                    } else {
                        updateTextBack(v.text.toString())
                    }
                    Navigation.findNavController(v).popBackStack()
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
}