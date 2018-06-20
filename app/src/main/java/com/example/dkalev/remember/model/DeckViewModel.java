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

    public DeckViewModel(DeckDataSource dataSource){
        mDataSource = dataSource;
    }

    public Flowable<List<Deck>> getAllDecks() {
        return mDataSource.getAllDecks();
    }

    public Flowable<Deck> getDeck(int deckId){
        return Flowable.zip(mDataSource.getAllDecks().flatMapIterable(list -> list)
                        .filter(d -> d.getDeckId() == deckId)
                ,mDataSource.getDeckCards(deckId),
                (deck, cards) -> {
            deck.setCards(cards);
            return deck;
        });
    }

    public Completable addDecks(Deck... decks){
        return Completable.fromAction(() -> {
            mDataSource.insertOrUpdateDeck(decks);
        });
    }

    public Completable deleteDeck(Deck deck) {
        return Completable.fromAction(() -> {
            mDataSource.deleteDeck(deck);
        });
    }

    //todo remove later
    public Completable addCard(Card card){
        return Completable.fromAction(() -> {
            mDataSource.insertCard(card);
        });
    }
}
