package com.example.dkalev.remember.model;

import android.arch.lifecycle.ViewModel;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class CardViewModel extends ViewModel {

    private final DeckDataSource mDataSource;
    private Flowable<Card> mCard;

    public CardViewModel(DeckDataSource dataSource){
        mDataSource = dataSource;
    }

    public Flowable<Card> getCard(int card_uid){
        if(mCard == null)
            mCard = mDataSource.getCard(card_uid);
        return mCard;
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
