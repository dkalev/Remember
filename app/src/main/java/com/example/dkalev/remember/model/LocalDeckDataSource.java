package com.example.dkalev.remember.model;

import java.util.List;

import io.reactivex.Flowable;

public class LocalDeckDataSource implements DeckDataSource {

    private final DeckDao mDeckDao;

    public LocalDeckDataSource(DeckDao deckDao){
        mDeckDao = deckDao;
    }


    @Override
    public Flowable<List<Deck>> getAllDecks() {
        return mDeckDao.getAll();
    }

    @Override
    public Flowable<List<Card>> getDeck(String name) {
        return mDeckDao.getCards(name);
    }

    @Override
    public Long[] insertOrUpdate(Deck... decks) {
        return mDeckDao.insertAll(decks);
    }

    @Override
    public Long insertDeck(Deck deck) {
        return mDeckDao.insertDeck(deck);
    }

    @Override
    public int deleteDeck(Deck deck) {
        mDeckDao.deleteCards(deck.getName());
        return mDeckDao.delete(deck);
    }

    @Override
    public void insertCard(Deck deck, Card card) {
        card.setDeckId(deck.getName());
        mDeckDao.insertCard(card);
    }

    @Override
    public void deleteCard(Card card) {
        mDeckDao.deleteCard(card.getUid());
    }


}
