package com.example.dkalev.remember.ui.deck

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.dkalev.remember.R
import com.example.dkalev.remember.ui.card.CardFlipFragment
import com.example.dkalev.remember.viewmodel.DeckViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_create_deck.*
import javax.inject.Inject

class CreateDeckFragment: Fragment(){

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var deckViewModel: DeckViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        deckViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(DeckViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_create_deck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newDeckButton.setOnClickListener {
            if (!TextUtils.isEmpty(deckNameEditText.text)) {
                val deckName = deckNameEditText.text.toString()
                compositeDisposable.add(deckViewModel
                        .addDeck(deckName)
                        .subscribe({
                            hideKeyboard(view)
                            //get back to decks fragment
                            val bundle = Bundle()
                            bundle.putString(CardFlipFragment.deckNameKey, deckName)
                            Navigation.findNavController(view).navigate(R.id.action_createDeckFragment_to_cardFlipFragment, bundle)
                        }, {
                                Toast.makeText(
                                        context,
                                        "A deck with the same name already exists",
                                        Toast.LENGTH_LONG).show()
                            }))
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
        compositeDisposable.dispose()
    }

    private fun hideKeyboard(view: View){
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}