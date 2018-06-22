package com.example.dkalev.remember.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DeckDao {

    @Query("SELECT * FROM deck")
    Flowable<List<Deck>> getAll();

    @Query("SELECT * FROM deck WHERE :deckId == deck_id")
    Flowable<Deck> getDeck(int deckId);

    @Insert
    void insertAll(Deck... decks);

    @Query("DELETE FROM deck WHERE :deckId == deck_id")
    void delete(int deckId);

    @Query("SELECT * FROM card WHERE :deckId == deck_id")
    Flowable<List<Card>> getCards(int deckId);
}
