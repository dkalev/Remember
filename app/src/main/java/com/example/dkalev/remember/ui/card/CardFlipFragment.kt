package com.example.dkalev.remember.flashcard

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
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
import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.viewmodel.DeckViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_card_flip.*
import javax.inject.Inject

class CardFlipFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: DeckViewModel

    private val disposable = CompositeDisposable()
    private val cards = ArrayList<Card>()
    private val cardsAdapter = CardsAdapter(cards)
    private val DEBUGTAG = "CardFlipFragment"
    private val recyclerLayout = "CardFlipFragment.recycler.layout"
    companion object {
        const val deckNameKey = "deckName"
    }
    private var listState: Parcelable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(DeckViewModel::class.java)
        setupRecyclerView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_card_flip, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        listState = cardRecyclerView.layoutManager!!.onSaveInstanceState()
        outState.putParcelable(recyclerLayout, listState)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(recyclerLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        val deckName = arguments!!.getString(deckNameKey)
        fetchDeckCards(deckName)
        if (listState != null)
            cardRecyclerView.layoutManager!!.onRestoreInstanceState(listState)
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


        cardRecyclerView.addOnItemTouchListener(CardRecyclerTouchListener(context!!, cardRecyclerView, object : CardRecyclerTouchListener.ClickListener {
            override fun onClick(view: FlashcardView) {
                Log.d(DEBUGTAG, "click")
                view.flipCard()
            }

            override fun onLongClick(view: FlashcardView?) {
                Log.d(DEBUGTAG, "longclick")
                val pos = view!!.tag as Int
                val card = cards[pos]
                val side = view.side
                Log.d(DEBUGTAG, "view pos :" + pos + "card uid: " + card.uid)
                //start edit card fragment
                val bundle = Bundle()
                bundle.putInt("cardUid", card.uid)
                bundle.putInt("cardSide", side)
                Navigation.findNavController(view).navigate(R.id.action_cardFlipFragment_to_editCardFragment, bundle)
            }

            override fun onSwipe(view: FlashcardView, swipe_type: Int) {
                Log.d(DEBUGTAG, "fling cards: ${cards.size}")
                if (cards.size > 1 && view.isFlipped) {
                    if (swipe_type == CardRecyclerTouchListener.SWIPE_OUT) {
                        view.animate()
                                .translationX(view.translationX * 10)
                                .translationY(view.translationY * 10)
                                .setInterpolator(AccelerateInterpolator())
                                .withEndAction {
                                    cards.removeAt(0)
                                    cardRecyclerView.adapter!!.notifyItemRemoved(0)
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
                    Navigation.findNavController(view).popBackStack()
                }
            }
        }))
    }

    private fun fetchDeckCards(deckName: String) {
        Log.d(DEBUGTAG, "fetchin da cardz...")
        disposable.add(viewModel.getDeck(deckName)
                .subscribe({ d ->
                    if (d.cards!!.isEmpty()){
                        //open edit card fragment
                        val bundle = Bundle()
                        bundle.putString(CardFlipFragment.deckNameKey, deckName)
                        Navigation.findNavController(activity!!. cardRecyclerView).navigate(R.id.action_cardFlipFragment_to_editCardFragment, bundle)
                    }
                    cards.clear()
                    cards.addAll(d.cards!!)
                    cardsAdapter.notifyDataSetChanged()
                },
                        { throwable -> Log.e(DEBUGTAG, "Unable to fetch cards", throwable) }))
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }
}