package com.example.dkalev.remember.model

import android.arch.lifecycle.ViewModel
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
            val card = Card(mCard!!.getDeckId())
            card.uid = mCard!!.getUid()
            card.textFront = textFront
            card.textBack = mCard!!.getTextBack()
            mCard = card
            mDataSource?.updateCard(mCard)
        }
    }

    fun setTextBack(textBack: String): Completable {
        return updateDB {
            val card = Card(mCard!!.getDeckId())
            card.uid = mCard!!.getUid()
            card.textFront = mCard!!.getTextFront()
            card.textBack = textBack
            mCard = card
            mDataSource?.updateCard(mCard)
        }
    }


    fun deleteCard(): Completable {
        return updateDB { mDataSource?.deleteCard(mCard) }
    }
}