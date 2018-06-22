package com.example.dkalev.remember.model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private DeckDataSource mDataSource;

    public ViewModelFactory(DeckDataSource dataSource) {
        mDataSource = dataSource;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if ( modelClass.isAssignableFrom(DeckViewModelKT.class))
            return (T) new DeckViewModelKT(mDataSource);

        if ( modelClass.isAssignableFrom(CardViewModelKT.class))
            return (T) new CardViewModelKT(mDataSource);

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
