package com.example.dkalev.remember.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DeckDao {

    @Query("SELECT * FROM deck")
    Flowable<List<Deck>> getAll();

    @Query("SELECT * FROM card WHERE :name == deck_id")
    Flowable<List<Card>> getCards(String name);

    @Insert
    void insertCard(Card card);

    @Query("DELETE FROM card WHERE :cardId == uid")
    void deleteCard(int cardId);

    @Query("SELECT * FROM card WHERE :card_uid == uid")
    Flowable<Card> getCard(int card_uid);

    @Insert
    Long[] insertAll(Deck... decks);

    @Insert
    Long insertDeck(Deck deck);

    @Query("DELETE FROM card WHERE :deckId == deck_id")
    void deleteCards(String deckId);

    @Delete
    int delete(Deck deck);

}
