package com.example.dkalev.remember.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
data class Deck(@field:PrimaryKey
           @field:ColumnInfo(name = "deck_name")
           var name: String) {

    @ColumnInfo(name = "size")
    var size: Int = 0

    @Ignore
    var cards: List<Card>? = null

    init {
        size = 0
    }
}
