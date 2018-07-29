package com.example.dkalev.remember.model.card

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Flowable

@Dao
interface CardDao {

    @Insert
    fun insertAll(vararg cards: Card)

    @Query("SELECT * FROM card WHERE :cardId == uid")
    fun getCard(cardId: Int): Flowable<Card>

    @Update
    fun updateCard(card: Card)


    @Query("DELETE FROM card WHERE :cardId == uid")
    fun deleteCard(cardId: Int)
}