package com.example.dkalev.remember.di


import com.example.dkalev.remember.ui.card.CardFlipFragment
import com.example.dkalev.remember.ui.card.EditCardFragment
import com.example.dkalev.remember.ui.deck.CreateDeckFragment
import com.example.dkalev.remember.ui.deck.DecksFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeCardFlipFragment(): CardFlipFragment

    @ContributesAndroidInjector
    abstract fun contributeEditCardFragment(): EditCardFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateDeckFragment(): CreateDeckFragment

    @ContributesAndroidInjector
    abstract fun contributeDecksFragment(): DecksFragment
}