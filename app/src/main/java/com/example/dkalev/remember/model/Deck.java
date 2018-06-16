package com.example.dkalev.remember.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

@Entity
public class Deck {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "deck_id")
    private int deckId;

    private String name;

    @ColumnInfo(name = "size")
    private int size;

    @Ignore
    private List<Card> cards;

    public Deck(String name) {
        super();
        this.name = name;
        size = 0;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String mName) {
        this.name = mName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @NonNull
    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(@NonNull int deckId) {
        this.deckId = deckId;
    }
}
