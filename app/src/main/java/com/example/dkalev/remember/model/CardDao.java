package com.example.dkalev.remember.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import io.reactivex.Flowable;

@Dao
public interface CardDao {

    @Insert
    void insertAll(Card... cards);

    @Query("SELECT * FROM card WHERE :cardId == uid")
    Flowable<Card> getCard(int cardId);

    @Update
    void updateCard(Card card);


    @Query("DELETE FROM card WHERE :cardId == uid")
    void deleteCard(int cardId);
}
