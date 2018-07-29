package com.example.dkalev.remember.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.dkalev.remember.viewmodel.CardViewModel
import com.example.dkalev.remember.viewmodel.DeckViewModel
import com.example.dkalev.remember.viewmodel.DecksViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DeckViewModel::class)
    abstract fun bindDeckViewModel(deckViewModel: DeckViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CardViewModel::class)
    abstract fun bindCardViewModel(cardViewModel: CardViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DecksViewModelFactory) : ViewModelProvider.Factory
}