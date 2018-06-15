package com.example.dkalev.remember.model;

import android.arch.lifecycle.ViewModel;

import org.intellij.lang.annotations.Flow;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
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

    public Completable addCard(Deck deck, Card card){
        return Completable.fromCallable(() -> {
            mDataSource.insertCard(deck, card);
            return "Done";
        });
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

    public Completable updateCard(Card card){
        return Completable.fromCallable(() -> {
            if (mDataSource.updateCard(card) > 0) {
                return "Done";
            }
            return "Error";
        });
    }

}
