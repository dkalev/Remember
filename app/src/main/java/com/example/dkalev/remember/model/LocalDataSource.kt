package com.example.dkalev.remember.model

import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.model.card.CardDao
import com.example.dkalev.remember.model.deck.Deck
import com.example.dkalev.remember.model.deck.DeckDao
import io.reactivex.Flowable
import javax.inject.Inject

class LocalDataSource @Inject constructor(
        private val mDeckDao: DeckDao,
        private val mCardDao: CardDao) : DataSource {


    override val allDecks: Flowable<List<Deck>>
        get() = mDeckDao.all

    override fun getDeck(deckName: String): Flowable<Deck> {
        return mDeckDao.getDeck(deckName)
    }

    override fun getDeckCards(deckName: String): Flowable<List<Card>> {
        return mDeckDao.getCards(deckName)
    }

    override fun insertOrUpdateDeck(vararg decks: Deck) {
        mDeckDao.insertAll(*decks)
    }


    override fun deleteDeck(deckName: String) {
        mDeckDao.delete(deckName)
    }


    override fun getCard(cardId: Int): Flowable<Card> {
        return mCardDao.getCard(cardId)
    }

    override fun insertCard(card: Card) {
        mCardDao.insertAll(card)
    }

    override fun updateCard(card: Card) {
        mCardDao.updateCard(card)
    }

    override fun deleteCard(card: Card) {
        mCardDao.deleteCard(card.uid)
    }


}