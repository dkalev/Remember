package com.example.dkalev.remember.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Delete
    void delete(Deck deck);

    @Query("SELECT * FROM card WHERE :deckId == deck_id")
    Flowable<List<Card>> getCards(int deckId);
}
