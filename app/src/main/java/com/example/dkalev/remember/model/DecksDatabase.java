package com.example.dkalev.remember.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Deck.class, Card.class}, version = 1)
public abstract class DecksDatabase extends RoomDatabase{

    private static volatile DecksDatabase INSTANCE;

    public abstract DeckDao deckDao();

    public static DecksDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DecksDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DecksDatabase.class, "Decks.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
