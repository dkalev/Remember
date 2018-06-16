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
        return mDataSource.getAllDecks();
    }

    public Flowable<Deck> getDeck(int deckId){
        return mDataSource.getDeck(deckId);
//        return Flowable.zip(mDataSource.getDeck(deckId),mDataSource.getDeckCards(deckId),(deck, cards) -> {
//            deck.setCards(cards);
//            return Flowable.just(deck);
//        });
    }

    public Flowable<List<Card>> getDeckCards(int deckId){
        return mDataSource.getDeckCards(deckId);
    }


    public Completable addDecks(Deck... decks){
        return Completable.fromCallable(() -> {
            mDataSource.insertOrUpdateDeck(decks);
            return "Done";
        });
    }

    public Completable deleteDeck(Deck deck) {
        return Completable.fromCallable(() -> {
            mDataSource.deleteDeck(deck);
            return "Done";
        });
    }

    public Flowable<Card> getCard(int card_uid){
        return mDataSource.getCard(card_uid);
    }

    public Completable addCard(Card card){
        return Completable.fromCallable(() -> {
            mDataSource.insertCard(card);
            return "Done";
        });
    }

    public Completable updateCard(Card card){
        return Completable.fromCallable(() -> {
            mDataSource.updateCard(card);
            return "Done";
        });
    }

    public Completable deleteCard(Card card){
        return Completable.fromCallable(() -> {
            mDataSource.deleteCard(card);
            return "Done";
        });
    }

}
