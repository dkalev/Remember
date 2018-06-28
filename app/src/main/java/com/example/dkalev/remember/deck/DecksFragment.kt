package com.example.dkalev.remember.deck

import android.arch.lifecycle.ViewModelProviders
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
import com.example.dkalev.remember.model.Card
import com.example.dkalev.remember.model.DeckViewModel
import com.example.dkalev.remember.model.Injection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_decks.*
import kotlinx.android.synthetic.main.content_decks.*

class DecksFragment: Fragment() {
    
    private var viewModel: DeckViewModel? = null
    private val disposable = CompositeDisposable()
    
    private val DEBUGTAG = "DecksFragment"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val vmf = Injection.provideViewModelFactory(context)
        viewModel = ViewModelProviders.of(activity!!, vmf).get(DeckViewModel::class.java)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_decks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_decksFragment_to_createDeckFragment)
        }
        setupRecyclerView(decksRecyclerView)
    }
    
    private fun setupRecyclerView(recyclerView: RecyclerView){
        recyclerView.adapter = DecksAdapter()
        
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.addOnItemTouchListener(DeckRecyclerTouchListener(context, recyclerView, object : DeckRecyclerTouchListener.ClickListener {
            override fun onClick(view: View) {
                //if you click not on item returns -1 and crashes
                if (recyclerView.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
                    Log.d(DEBUGTAG, "click")
                    val bundle = Bundle()
                    val deckId = view.tag as Int
                    bundle.putInt("deckId", deckId)
                    Navigation.findNavController(view).navigate(R.id.action_decksFragment_to_cardFlipFragment, bundle)
                }
            }

            override fun onLongClick(view: View) {
                Log.d(DEBUGTAG, "long click")
                disposable.add(viewModel!!.deleteDeck(view.tag as Int)
                        .subscribe({
                            Toast.makeText(
                                    context,
                                    "Deck deleted",
                                    Toast.LENGTH_LONG).show()
                        },
                                { throwable -> Log.e(DEBUGTAG, "Unable to delete deck", throwable) }))
            }

            override fun onFling(view: View, e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float) {
                Log.d(DEBUGTAG, "fling")
                val card = Card(view.tag as Int)
                card.textFront = "Front"
                card.textBack = "Back"
                disposable.add(viewModel!!.addCard(card)
                        .subscribe({ Log.d(DEBUGTAG, "Card added") }, { throwable -> Log.e(DEBUGTAG, "Unable to add deck", throwable) }))
            }
        }))
    }

    private fun getAllDecks(){
        disposable.add(viewModel!!.getAllDecks()
        !!.subscribe({decks ->
            run {
                val adapter = decksRecyclerView.adapter as DecksAdapter
                adapter.setDeckList(decks)
            }
        },
                {throwable -> Log.e(DEBUGTAG, "Unable to get decks", throwable)}
        ))
    }

    override fun onStart() {
        super.onStart()
            getAllDecks()
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}