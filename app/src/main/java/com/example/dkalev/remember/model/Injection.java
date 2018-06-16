package com.example.dkalev.remember.model;

import android.content.Context;

public class Injection {

    public static DeckDataSource provideUserDataSource(Context context) {
        DecksDatabase database = DecksDatabase.getInstance(context);
        return new LocalDeckDataSource(database.deckDao(), database.cardDao());
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        DeckDataSource dataSource = provideUserDataSource(context);
        return new ViewModelFactory(dataSource);
    }
}
