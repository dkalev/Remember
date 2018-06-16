package com.example.dkalev.remember.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.sql.Blob;

@Entity(foreignKeys = @ForeignKey(
        entity = Deck.class,
        parentColumns = "deck_id",
        childColumns = "deck_id"))
public class Card {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "text_front")
    private String textFront;

    @ColumnInfo(name = "text_back")
    private String textBack;

    @ColumnInfo(name = "deck_id")
    private int deckId;

    public Card(int deckId){
        this.deckId = deckId;
    }


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

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public String getTextFront() {
        return textFront;
    }

    public String getTextBack() {
        return textBack;
    }

    public void setTextFront(String textFront) {
        this.textFront = textFront;
    }

    public void setTextBack(String textBack) {
        this.textBack = textBack;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }
}
