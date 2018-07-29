package com.example.dkalev.remember.ui.deck

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.dkalev.remember.R
import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.ui.card.CardFlipFragment
import com.example.dkalev.remember.viewmodel.DeckViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_decks.*
import kotlinx.android.synthetic.main.deck_item.view.*
import javax.inject.Inject

class DecksFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var deckViewModel: DeckViewModel

    @Inject
    lateinit var disposable: CompositeDisposable

    private val DEBUGTAG = "DecksFragment"

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        deckViewModel = ViewModelProviders.of(this, viewModelFactory).get(DeckViewModel::class.java)
        getAllDecks()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_decks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_decksFragment_to_createDeckFragment)
        }
        initRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    
    private fun initRecyclerView(){
        decksRecyclerView.adapter = DecksAdapter()
        
        decksRecyclerView.layoutManager = LinearLayoutManager(context)

        decksRecyclerView.addOnItemTouchListener(DeckRecyclerTouchListener(context!!, decksRecyclerView, object : DeckRecyclerTouchListener.ClickListener {
            override fun onClick(view: View?) {
                //if you click not on item returns -1 and crashes
                if (view != null && decksRecyclerView.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
                    Log.d(DEBUGTAG, "click")
                    val bundle = Bundle()
                    //todo find a better way to get the deck name
                    val deckName = view.deckNameTextView.text.toString()
                    bundle.putString(CardFlipFragment.deckNameKey, deckName)
                    Navigation.findNavController(view).navigate(R.id.action_decksFragment_to_cardFlipFragment, bundle)
                }
            }

            override fun onLongClick(view: View?) {
                Log.d(DEBUGTAG, "long click")
                disposable.add(deckViewModel.deleteDeck(view!!.deckNameTextView.text.toString())
                        .subscribe({
                            decksRecyclerView.adapter!!.notifyItemRemoved(view.tag as Int)
                            Toast.makeText(
                                    context,
                                    "Deck ${view.tag} deleted",
                                    Toast.LENGTH_LONG).show()
                        }, { throwable -> Log.e(DEBUGTAG, "Unable to delete deck", throwable) }))
            }

            override fun onFling(view: View?, e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float) {
                Log.d(DEBUGTAG, "fling")
                val card = Card(view!!.deckNameTextView.text.toString())
                card.textFront = "Front"
                card.textBack = "Back"
                disposable.add(deckViewModel.addCard(card)
                        .subscribe({
                            Log.d(DEBUGTAG, "Card added")
                        }, { throwable -> Log.e(DEBUGTAG, "Unable to add deck", throwable) }))
            }
        }))
    }

    private fun getAllDecks(){
        Log.d(DEBUGTAG, "gettin deckin")
        disposable.add(
            deckViewModel.getAllDecks()!!
            .subscribe({decks ->
                val adapter = decksRecyclerView.adapter as DecksAdapter
                adapter.setDeckList(decks)
            }, {throwable -> Log.e(DEBUGTAG, "Unable to get decks", throwable)}))
    }
}