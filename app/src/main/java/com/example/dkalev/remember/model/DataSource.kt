package com.example.dkalev.remember.model

import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.model.deck.Deck
import io.reactivex.Flowable

interface DataSource {

    val allDecks: Flowable<List<Deck>>

    fun getDeckCards(deckName: String): Flowable<List<Card>>

    fun getDeck(deckName: String): Flowable<Deck>

    fun insertOrUpdateDeck(vararg decks: Deck)

    fun deleteDeck(deckName: String)

    fun getCard(cardId: Int): Flowable<Card>

    fun insertCard(card: Card)

    fun updateCard(card: Card)

    fun deleteCard(card: Card)
}