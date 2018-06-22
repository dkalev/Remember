package com.example.dkalev.remember.model;

import java.util.List;

import io.reactivex.Flowable;

public interface DeckDataSource {

    Flowable<List<Deck>> getAllDecks();

    Flowable<List<Card>> getDeckCards(int deckId);

    Flowable<Deck> getDeck(int deckId);

    void insertOrUpdateDeck(Deck... decks);

    void deleteDeck(int deckId);

    Flowable<Card> getCard(int carId);

    void insertCard(Card card);

    void updateCard(Card card);

    void deleteCard(Card card);
}
