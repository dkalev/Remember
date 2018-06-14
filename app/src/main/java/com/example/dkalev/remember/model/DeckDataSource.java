package com.example.dkalev.remember.model;

import java.util.List;

import io.reactivex.Flowable;

public interface DeckDataSource {

    Flowable<List<Deck>> getAllDecks();

    Flowable<List<Card>> getDeck(String name);

    Long[] insertOrUpdate(Deck... decks);

    Long insertDeck(Deck deck);

    int deleteDeck(Deck deck);

    void insertCard(Deck deck, Card card);

    void deleteCard(Card card);
}
