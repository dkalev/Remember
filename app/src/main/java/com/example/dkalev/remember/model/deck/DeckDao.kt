package com.example.dkalev.remember.model.deck

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.dkalev.remember.model.card.Card
import io.reactivex.Flowable
import java.util.*

@Dao
interface DeckDao {

    @get:Query("SELECT * FROM deck")
    val all: Flowable<List<Deck>>

    @Query("SELECT * FROM deck WHERE :deckName == deck_name")
    fun getDeck(deckName: String): Flowable<Deck>

    @Insert
    fun insertAll(vararg decks: Deck)

    @Query("DELETE FROM deck WHERE :deckName == deck_name")
    fun delete(deckName: String)

    @Query("SELECT * FROM card WHERE :deckName == deck_name")
    fun getCards(deckName: String): Flowable<List<Card>>

    @Query("SELECT * FROM card WHERE :deckName == deck_name AND :date >= due_date")
    fun getDueCards(deckName: String, date: Date): Flowable<List<Card>>
}