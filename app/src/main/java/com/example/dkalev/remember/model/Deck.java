package com.example.dkalev.remember.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

@Entity
public class Deck {
    @PrimaryKey
    @NonNull
    private String name;

    private int size;

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String mName) {
        this.name = mName;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Deck(String name) {
        this.name = name;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    @Ignore
    private List<Card> cards;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
