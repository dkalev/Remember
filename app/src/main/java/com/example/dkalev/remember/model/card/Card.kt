package com.example.dkalev.remember.model.card

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import com.example.dkalev.remember.model.deck.Deck
import java.util.*

@Entity(foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = arrayOf("deck_name"),
        childColumns = arrayOf("deck_name"),
        onDelete = CASCADE)])//onDelete = CASCADE -> delete all cards when deleting the deck
data class Card(@field:ColumnInfo(name = "deck_name")
           var deckName: String?) {

    //    public void setImageFront(Bitmap imageFront) {
    //        this.imageFront = imageFront;
    //    }
    //
    //    public void setImageBack(Bitmap imageBack) {
    //        this.imageBack = imageBack;
    //    }

    //    @ColumnInfo(name = "image_front")
    //
    //    private Bitmap imageFront;
    //
    //    @ColumnInfo(name = "image_back")
    //    private Bitmap imageBack;

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "text_front")
    var textFront: String? = null

    @ColumnInfo(name = "text_back")
    var textBack: String? = null

    @ColumnInfo(name = "difficulty")
    var difficulty: Double = 0.3

    @ColumnInfo(name = "days_between_reviews")
    var daysBetweenReviews: Int = 0

    @ColumnInfo(name = "day_last_reviewed")
    var dayLastReviewed: Date? = null

    @ColumnInfo(name = "due_date")
    var dueDate: Date? = null
}