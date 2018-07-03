package com.example.dkalev.remember.deck

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.dkalev.remember.R
import com.example.dkalev.remember.flashcard.CardFlipFragment
import com.example.dkalev.remember.model.DeckViewModel
import com.example.dkalev.remember.model.Injection
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_create_deck.*

class CreateDeckFragment: Fragment(){

    private var viewModel: DeckViewModel? = null
    private var d: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_create_deck, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val vmf = Injection.provideViewModelFactory(context!!)
        viewModel = ViewModelProviders.of(activity!!, vmf).get(DeckViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newDeckButton.setOnClickListener {
            if (!TextUtils.isEmpty(deckNameEditText.text)) {
                val deckName = deckNameEditText.text.toString()
                d = viewModel!!
                        .addDeck(deckName)
                        .subscribe({
                            //hide keyboard
                            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
                            //get back to decks fragment
                            val bundle = Bundle()
                            bundle.putString(CardFlipFragment.deckNameKey, deckName)
                            Navigation.findNavController(view).navigate(R.id.action_createDeckFragment_to_cardFlipFragment, bundle)
                        },
                                { throwable ->
                                    Log.e("CreateDeckActivity", "Unable to add deck", throwable)
                                })
            }else{
                Toast.makeText(
                        context,
                        "Deck name is empty",
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (d != null)
            d!!.dispose()
    }
}