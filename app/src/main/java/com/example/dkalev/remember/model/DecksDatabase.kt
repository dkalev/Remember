package com.example.dkalev.remember.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.model.card.CardDao
import com.example.dkalev.remember.model.deck.Deck
import com.example.dkalev.remember.model.deck.DeckDao

@Database(entities = [(Deck::class), (Card::class)], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class DecksDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

}