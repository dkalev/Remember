package com.example.dkalev.remember.flashcard

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.navigation.Navigation
import com.example.dkalev.remember.R
import com.example.dkalev.remember.model.Card
import com.example.dkalev.remember.model.DeckViewModel
import com.example.dkalev.remember.model.Injection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_card_flip.*

class CardFlipFragment: Fragment() {

    private var viewModel: DeckViewModel? = null
    private val disposable = CompositeDisposable()
    private val cards = ArrayList<Card>()
    private val cardsAdapter = CardsAdapter(cards)
    private val DEBUGTAG = "CardFlipFragment"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val vmf = Injection.provideViewModelFactory(context)
        viewModel = ViewModelProviders.of(activity!!, vmf).get(DeckViewModel::class.java)
        fetchDeckCards(arguments!!.getInt("deckId"))
        setupRecyclerView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_card_flip, container, false)
    }

    private fun setupRecyclerView() {
        cardRecyclerView.adapter = cardsAdapter

        cardRecyclerView.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        cardRecyclerView.itemAnimator = object : DefaultItemAnimator() {
            //handle disappearance by swiping the cards
            override fun animateDisappearance(viewHolder: RecyclerView.ViewHolder, preLayoutInfo: RecyclerView.ItemAnimator.ItemHolderInfo, postLayoutInfo: RecyclerView.ItemAnimator.ItemHolderInfo?): Boolean {
                return false
            }
        }


        cardRecyclerView.addOnItemTouchListener(CardRecyclerTouchListener(context, cardRecyclerView, object : CardRecyclerTouchListener.ClickListener {
            override fun onClick(view: FlashcardView) {
                Log.d(DEBUGTAG, "click")
                view.flipCard()
            }

            override fun onLongClick(view: FlashcardView) {
                Log.d(DEBUGTAG, "longclick")
                val pos = view.tag as Int
                //should work because passing reference to cards
                val card = cards[pos]
                val side = view.side
                Log.d(DEBUGTAG, "view pos :" + pos + "card uid: " + card.uid)
                //todo start edit card activity
                val bundle = Bundle()
                bundle.putInt("cardUid", card.uid)
                bundle.putInt("cardSide", side)
                Navigation.findNavController(view).navigate(R.id.action_cardFlipFragment_to_editCardFragment, bundle)
                //startEditCardActivity(view, card.getUid(), side)
            }

            override fun onSwipe(view: FlashcardView, swipe_type: Int) {
                Log.d(DEBUGTAG, "fling")
                if (cards.size > 0 && view.isFlipped) {
                    if (swipe_type == CardRecyclerTouchListener.SWIPE_OUT) {
                        view.animate()
                                .translationX(view.translationX * 10)
                                .translationY(view.translationY * 10)
                                .setInterpolator(AccelerateInterpolator())
                                .withEndAction {
                                    cards.removeAt(0)
                                    cardRecyclerView.getAdapter().notifyItemRemoved(0)
                                }
                                .start()
                    } else {
                        view.animate()
                                .translationX(0f)
                                .translationY(0f)
                                .setInterpolator(AccelerateDecelerateInterpolator())
                                .start()
                    }
                } else {
                    //todo open next activity when out of cards
                }
            }
        }))
    }

    private fun fetchDeckCards(deckId: Int) {
        disposable.add(viewModel!!.getDeck(deckId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ d ->
                    cards.clear()
                    cards.addAll(d.cards)
                    cardsAdapter.notifyDataSetChanged()
                },
                        { throwable -> Log.e(DEBUGTAG, "Unable to fetch cards", throwable) }))
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}