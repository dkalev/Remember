package com.example.dkalev.remember.model

import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CardViewModel(dataSource: DeckDataSource) : ViewModel() {

    private var mDataSource: DeckDataSource? = null
    private var mCard: Card? = null

    init {
        mDataSource = dataSource
    }

    private fun updateDB(func: () -> Unit): Completable {
        return Completable.fromAction{ func() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun fetchFromDB(func: () -> Flowable<String>): Flowable<String> {
        return func()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTextFront(card_uid: Int): Flowable<String> {
        return fetchFromDB { mDataSource!!.getCard(card_uid)
                .map { card ->
                    mCard = card
                    card.textFront
                }
        }
    }

    fun getTextBack(card_uid: Int): Flowable<String> {
        return fetchFromDB {
            mDataSource!!.getCard(card_uid)
                    .map { card ->
                        mCard = card
                        card.textBack
                    }
        }
    }

    fun setTextFront(textFront: String): Completable {
        return updateDB {
            val card = Card(mCard!!.deckName)
            card.uid = mCard!!.uid
            card.textFront = textFront
            card.textBack = mCard!!.textBack
            mCard = card
            mDataSource?.updateCard(mCard!!)
        }
    }

    fun setTextBack(textBack: String): Completable {
        return updateDB {
            val card = Card(mCard!!.deckName)
            card.uid = mCard!!.uid
            card.textFront = mCard!!.textFront
            card.textBack = textBack
            mCard = card
            mDataSource?.updateCard(mCard!!)
        }
    }

    fun createCard(deckName: String): Completable {
        if (mCard == null) {
            val card = Card(deckName)
            card.textFront = "Front"
            card.textBack = "Back"
            mCard = card
            Log.d("CardViewModel", "created new card")
        }
        return updateDB { mDataSource?.insertCard(mCard!!)}
    }

    fun deleteCard(): Completable {
        return updateDB { mDataSource?.deleteCard(mCard!!) }
    }
}