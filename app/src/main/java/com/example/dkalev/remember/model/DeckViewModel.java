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

    private Flowable<List<Deck>> decks;

    public DeckViewModel(DeckDataSource dataSource){
        mDataSource = dataSource;
    }

    public Flowable<List<Deck>> getAllDecks() {
        if(decks == null)
            decks = mDataSource.getAllDecks();
        return decks;
    }

    public Flowable<Deck> getDeck(int deckId){
        if(decks == null)
            getAllDecks();
        return Flowable.zip(decks.flatMapIterable(list -> list)
                        .filter(d -> d.getDeckId() == deckId)
                ,mDataSource.getDeckCards(deckId),
                (deck, cards) -> {
            deck.setCards(cards);
            return deck;
        });
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

    //todo remove later
    public Completable addCard(Card card){
        return Completable.fromCallable(() -> {
            mDataSource.insertCard(card);
            return "Done";
        });
    }
}
