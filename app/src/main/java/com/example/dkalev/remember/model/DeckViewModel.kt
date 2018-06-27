package com.example.dkalev.remember.model

import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class DeckViewModel(dataSource: DeckDataSource) : ViewModel() {
    private var mDataSource: DeckDataSource? = null
    private var mDecks: Flowable<List<Deck>>? = null

    init {
        mDataSource = dataSource
    }

    private fun updateDB(func: () -> Unit): Completable {
        return Completable.fromAction{ func() }
//                .autoDisposable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun loadDecks() {
        mDecks = mDataSource!!
                .allDecks
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun getAllDecks(): Flowable<List<Deck>>? {
        if (mDecks == null)
            loadDecks()
        return mDecks
    }

    fun getDeck(deckId: Int): Flowable<Deck> {
        if (mDecks == null)
            loadDecks()
        return Flowable.zip(
                mDecks!!.flatMapIterable { list -> list }
                        .filter { d -> d.deckId == deckId },
                mDataSource!!.getDeckCards(deckId),
                BiFunction{deck: Deck, cards: List<Card> ->
                    deck.cards = cards
                 deck})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun addDeck(deckName: String): Completable {
        val deck = Deck(deckName)
        return updateDB { mDataSource?.insertOrUpdateDeck(deck) }
    }

    fun deleteDeck(deckId: Int): Completable {
        return updateDB { mDataSource?.deleteDeck(deckId) }
    }

    //todo remove later
    fun addCard(card: Card): Completable {
        return updateDB { mDataSource?.insertCard(card) }
    }
}