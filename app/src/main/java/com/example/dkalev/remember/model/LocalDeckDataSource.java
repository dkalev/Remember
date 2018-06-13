package com.example.dkalev.remember;

import com.example.dkalev.remember.model.Card;
import com.example.dkalev.remember.model.Deck;
import com.example.dkalev.remember.model.DeckDao;
import com.example.dkalev.remember.model.DeckDataSource;

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
    public void insertOrUpdate(Deck deck) {
        mDeckDao.insertAll(deck);
    }

    @Override
    public void deleteDeck(Deck deck) {
        mDeckDao.delete(deck);
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
