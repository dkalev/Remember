package com.example.dkalev.remember.deck;

class Deck {

    private final String mDeckName;
    private int mNumCards;

    public Deck(String name) {
        mDeckName = name;
        mNumCards = 0;
    }

    public int getSize(){
        return mNumCards;
    }

    public String getDeckName() {
        return mDeckName;
    }

    public int getNumCards() {
        return mNumCards;
    }
}
