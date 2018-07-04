package com.example.dkalev.remember.flashcard

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.dkalev.remember.R
import com.example.dkalev.remember.viewmodel.CardViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_edit_card.*
import javax.inject.Inject


class EditCardFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var cardViewModel: CardViewModel
    private val disposable = CompositeDisposable()

    private var cardUid = 0
    private var cardSide = 0
    private var deckName = ""

    private val DEBUGTAG = "EditCardFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_edit_card, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cardViewModel = ViewModelProviders.of(this, viewModelFactory).get(CardViewModel::class.java)

        cardUid = arguments!!.get("cardUid") as Int
        cardSide = arguments!!.get("cardSide") as Int
        deckName = arguments!!.get(CardFlipFragment.deckNameKey) as String

        if (cardUid == -1)
            createCard(deckName)
        else
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    private fun getCard(card_uid: Int) {
        Log.d(DEBUGTAG, "card uid: $card_uid")
        disposable.add(cardViewModel.getTextFront(card_uid)
                .subscribe({ textFront ->cardFrontEditText!!.setText(textFront)}
                ) { throwable -> Log.e(DEBUGTAG, "Unable to retrieve card", throwable) })

        disposable.add(cardViewModel.getTextBack(card_uid)
                .subscribe { textBack -> cardBackEditText!!.setText(textBack)
                    progressBar.visibility = View.GONE
                })
    }

    private fun createCard(deckName: String){
        disposable.add(cardViewModel
                .createCard(deckName)
                .subscribe({
                    Toast.makeText(
                            context,
                            "Created new card",
                            Toast.LENGTH_LONG).show()
                }
                ) { throwable -> Log.e(DEBUGTAG, "Unable to create new card", throwable) })
    }

    private fun updateTextFront(textFront: String, v: View) {
        disposable.add(cardViewModel.setTextFront(textFront)
//                .autoDisposable()
                .subscribe({
                    Toast.makeText(
                            context,
                            "Updated front with: $textFront",
                            Toast.LENGTH_LONG).show()
                    Navigation.findNavController(v).popBackStack()
                }
                ) { throwable -> Log.e(DEBUGTAG, "Unable to update text front", throwable) })
    }

    private fun updateTextBack(textBack: String, v: View) {
        disposable.add(cardViewModel.setTextBack(textBack)
                .subscribe({
                    Toast.makeText(
                            context,
                            "Updated back with: $textBack",
                            Toast.LENGTH_LONG).show()
                    Navigation.findNavController(v).popBackStack()

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
                            event.action == KeyEvent.ACTION_DOWN &&
                            event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (event == null || !event.isShiftPressed) {
                    // the user is done typing.
                    //hide the keyboard
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    updateCard(v.text.toString(), v)
                    return true // consume.
                }
            }
            return false // pass on to other listeners.
        }
    }

    private fun updateCard(text: String, v: View){
        if (cardSide == 0) {
            updateTextFront(text, v)
        } else {
            updateTextBack(text, v)
        }
    }

    private inner class TextOnFocusChangeListener : View.OnFocusChangeListener {

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            if (!hasFocus) {
                //updateCard((v as TextView).text.toString(), v)
            }
        }
    }
}