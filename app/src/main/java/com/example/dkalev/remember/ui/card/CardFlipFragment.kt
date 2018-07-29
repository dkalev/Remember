package com.example.dkalev.remember.ui.card

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
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
import com.example.dkalev.remember.viewmodel.CardViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_card_flip.*
import javax.inject.Inject

class CardFlipFragment: Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: CardViewModel

    @Inject
    lateinit var disposable: CompositeDisposable

    private val cards = ArrayList<Card>()
    private var cardStates: ArrayList<Int>? = null
    private val cardsAdapter = CardsAdapter(cards)
    private val DEBUGTAG = "CardFlipFragment"
    companion object {
        const val deckNameKey = "deckName"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(CardViewModel::class.java)
        setupRecyclerView()

        if (savedInstanceState != null && savedInstanceState.containsKey("cardStates")){
            cardStates = savedInstanceState.getIntegerArrayList("cardStates")
            cardsAdapter.cardState = cardStates
        }

        val deckName = arguments!!.getString(deckNameKey)
        fetchDeckCards(deckName)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("cardStates", cardStates);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_card_flip, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }


    private fun setupRecyclerView() {
        cardRecyclerView.adapter = cardsAdapter

        cardRecyclerView.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

            override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {

                val smoothScroller = object: LinearSmoothScroller(context){
                    //return a vector pointing to the next position -> to the card to the right
                    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                        return PointF(1F, 0F)
                    }

                }

                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }

        cardRecyclerView.addOnItemTouchListener(CardRecyclerTouchListener(context!!, cardRecyclerView, object : CardRecyclerTouchListener.ClickListener {
            override fun onClick(view: FlashcardView) {
                Log.d(DEBUGTAG, "click")
                view.flipCard()
                if (cardStates == null){
                    cardStates = ArrayList()
                }
                    cardStates!!.add(view.tag as Int, view.side)
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
                if (view.isFlipped) {
                    if (swipe_type == CardRecyclerTouchListener.SWIPE_IN) {
                        animateIn(view)
                    } else {
                        animateOut(view) {
                            val nextCardPos = view.tag as Int + 1
                            cardRecyclerView.smoothScrollToPosition(nextCardPos)

                            if (nextCardPos == cards.size)
                                Navigation.findNavController(view).popBackStack()
                        }
                    }
                }
            }
        }))
    }

    private fun animateIn(view: View){
        view.animate()
                .translationX(0f)
                .translationY(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }

    private fun animateOut(view: View, doAfterAnimateOut: () -> Unit){
        view.animate()
                .translationX(view.translationX * 10)
                .translationY(view.translationY * 10)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    doAfterAnimateOut()
                }
                .start()
    }

    private fun fetchDeckCards(deckName: String) {
        Log.d(DEBUGTAG, "fetchin da cardz...")
        disposable.add(
            viewModel.getCards(deckName)
                .subscribe({ deck ->
                    if (deck.isEmpty())
                        startEditCardFragment(deckName)

                    cards.clear()
                    cards.addAll(deck)
                    cardsAdapter.notifyDataSetChanged()
                }, { throwable -> Log.e(DEBUGTAG, "Unable to fetch cards", throwable) }))
    }

    private fun startEditCardFragment(deckName: String){
        val bundle = Bundle()
        bundle.putString(CardFlipFragment.deckNameKey, deckName)
        Navigation.findNavController(activity!!. cardRecyclerView).navigate(R.id.action_cardFlipFragment_to_editCardFragment, bundle)
    }

}