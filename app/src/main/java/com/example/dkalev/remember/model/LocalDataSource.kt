package com.example.dkalev.remember.model

import io.reactivex.Flowable

class LocalDeckDataSource(private val mDeckDao: DeckDao, private val mCardDao: CardDao) : DeckDataSource {


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