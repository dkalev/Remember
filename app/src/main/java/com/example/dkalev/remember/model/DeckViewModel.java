package com.example.dkalev.remember.model;

import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.internal.operators.completable.CompletableToFlowable;

public class DeckViewModel extends ViewModel {

    private final DeckDataSource mDataSource;

    private Flowable<List<Deck>> mDecks;

    public DeckViewModel(DeckDataSource dataSource){
        mDataSource = dataSource;
    }

    public Flowable<List<Deck>> getAllDecks() {
        mDecks = mDataSource.getAllDecks();
        return mDecks;
    }

    public Flowable<List<Card>> getDeckCards(String deckName){
        return mDataSource.getDeck(deckName);
    }

    public void addCard(Deck deck, Card card){
        mDataSource.insertCard(deck, card);
    }

    public Flowable<Card> getCard(int card_uid){
        return mDataSource.getCard(card_uid);
    }
    public void deleteCard(Card card){
        mDataSource.deleteCard(card);
    }

    public Single<Long[]> addDecks(Deck... decks){
        return Single.fromCallable(() -> mDataSource.insertOrUpdate(decks));
    }

    public Single<Long> addDeck(Deck deck){
        return Single.fromCallable(() -> mDataSource.insertDeck(deck));
    }

    public Single<Integer> deleteDeck(Deck deck) {
        return Single.fromCallable(() -> mDataSource.deleteDeck(deck));
    }

}
