package com.example.dkalev.remember.viewmodel

import android.arch.lifecycle.ViewModel
import com.example.dkalev.remember.model.DataSource
import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.model.deck.Deck
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DeckViewModel @Inject constructor(
        private val dataSource: DataSource) : ViewModel() {

    private val decks = dataSource.allDecks

    private fun updateDB(func: () -> Unit): Completable {
        return Completable.fromAction{ func() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllDecks(): Flowable<List<Deck>>? {
        return decks
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun addDeck(deckName: String): Completable {
        val deck = Deck(deckName)
        return updateDB { dataSource.insertOrUpdateDeck(deck) }
    }

    fun deleteDeck(deckName: String): Completable {
        return updateDB { dataSource.deleteDeck(deckName) }
    }

    //todo remove later
    fun addCard(card: Card): Completable {
        return updateDB { dataSource.insertCard(card) }
    }
}