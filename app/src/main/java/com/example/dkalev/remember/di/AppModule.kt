package com.example.dkalev.remember.di

import android.app.Application
import android.arch.persistence.room.Room
import com.example.dkalev.remember.model.DataSource
import com.example.dkalev.remember.model.DecksDatabase
import com.example.dkalev.remember.model.LocalDataSource
import com.example.dkalev.remember.model.card.CardDao
import com.example.dkalev.remember.model.deck.DeckDao
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideDb(app: Application) : DecksDatabase{
        return Room
                .databaseBuilder(app, DecksDatabase::class.java, "Decks.db")
                .build()
    }

    @Singleton
    @Provides
    fun providesDeckDao(db: DecksDatabase): DeckDao{
        return db.deckDao()
    }

    @Singleton
    @Provides
    fun providesCardDao(db: DecksDatabase): CardDao {
        return db.cardDao()
    }

    @Singleton
    @Provides
    fun providesLocalDataSource(deckDao: DeckDao, cardDao: CardDao) : DataSource {
        return LocalDataSource(deckDao, cardDao)
    }

    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

}