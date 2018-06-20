package com.example.dkalev.remember.model;

import android.arch.lifecycle.ViewModel;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class CardViewModel extends ViewModel {

    private final DeckDataSource mDataSource;
    private Card mCard;

    public CardViewModel(DeckDataSource dataSource){
        mDataSource = dataSource;
    }

    public Flowable<String> getTextFront(int card_uid){
        return mDataSource.getCard(card_uid)
                .map(card -> {
                    mCard = card;
                    return card.getTextFront();
                });
    }

    public Flowable<String> getTextBack(int card_uid){
        return mDataSource.getCard(card_uid)
                .map(card -> {
                    mCard = card;
                    return card.getTextBack();
                });
    }

    public Completable setTextFront(final String textFront){
        return Completable.fromAction(()->{
            Card card = new Card(mCard.getDeckId());
            card.setUid(mCard.getUid());
            card.setTextFront(textFront);
            card.setTextBack(mCard.getTextBack());
            mCard = card;
            mDataSource.updateCard(mCard);
        });
    }

    public Completable setTextBack(final String textBack){
        return Completable.fromAction(()->{
            Card card = new Card(mCard.getDeckId());
            card.setUid(mCard.getUid());
            card.setTextFront(mCard.getTextFront());
            card.setTextBack(textBack);
            mCard = card;
            mDataSource.updateCard(mCard);
        });
    }


    public Completable deleteCard(){
        return Completable.fromAction(() -> mDataSource.deleteCard(mCard));
    }
}
