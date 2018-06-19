package com.example.dkalev.remember.model;

import java.util.List;

import io.reactivex.Flowable;

public class LocalDeckDataSource implements DeckDataSource {

    private final DeckDao mDeckDao;

    private final CardDao mCardDao;

    public LocalDeckDataSource(DeckDao deckDao, CardDao cardDao){
        mDeckDao = deckDao;
        mCardDao = cardDao;
    }


    @Override
    public Flowable<List<Deck>> getAllDecks() {
        return mDeckDao.getAll();
    }

    @Override
    public Flowable<Deck> getDeck(int deckId) {
        return mDeckDao.getDeck(deckId);
    }

    @Override
    public Flowable<List<Card>> getDeckCards(int deckId) {
        return mDeckDao.getCards(deckId);
    }

    @Override
    public void insertOrUpdateDeck(Deck... decks) {
        mDeckDao.insertAll(decks);
    }


    @Override
    public void deleteDeck(Deck deck) {
        mDeckDao.delete(deck);
    }


    @Override
    public Flowable<Card> getCard(int card_uid) {
        return mCardDao.getCard(card_uid);
    }

    @Override
    public void insertCard(Card card) {
        mCardDao.insertAll(card);
    }

    @Override
    public void updateCard(Card card) {
        mCardDao.updateCard(card);
    }

    @Override
    public void deleteCard(Card card) {
        mCardDao.deleteCard(card.getUid());
    }





}
