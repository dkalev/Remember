package com.example.dkalev.remember.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.dkalev.remember.model.DataSource
import com.example.dkalev.remember.model.card.Card
import com.example.dkalev.remember.srs.CardReviewScheduler
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class CardViewModel @Inject constructor(
        private val dataSource: DataSource) : ViewModel() {

    private var mCard: Card? = null
    private var cards: Flowable<List<Card>>? = null
    private var curDeck: String? = null

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

    fun getCards(deckName: String): Flowable<List<Card>> {
        if (curDeck == null || deckName != curDeck){
            curDeck = deckName
            cards = dataSource.getDeckCards(deckName)
            Log.d("CardViewModel", "from the db")
        }
        return cards as Flowable<List<Card>>
    }

    fun getTextFront(card_uid: Int): Flowable<String> {
        cards
        return fetchFromDB { dataSource.getCard(card_uid)
                .map { card ->
                    mCard = card
                    card.textFront
                }
        }
    }

    fun getTextBack(card_uid: Int): Flowable<String> {
        return fetchFromDB {
            dataSource.getCard(card_uid)
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
            dataSource.updateCard(mCard!!)
        }
    }

    fun setTextBack(textBack: String): Completable {
        return updateDB {
            val card = Card(mCard!!.deckName)
            card.uid = mCard!!.uid
            card.textFront = mCard!!.textFront
            card.textBack = textBack
            mCard = card
            dataSource.updateCard(mCard!!)
        }
    }

    fun createCard(deckName: String): Completable {
        if (mCard == null) {
            val card = Card(deckName)
            card.textFront = "Front"
            card.textBack = "Back"
            mCard = card
        }
        return updateDB { dataSource.insertCard(mCard!!)}
    }

    fun reviewCard(answeredCorrect: Boolean): Completable{
        if (mCard!!.dayLastReviewed == null){
            mCard!!.dayLastReviewed = Date()
        }
        val dueDate = CardReviewScheduler.scheduleCard(mCard!!.difficulty, answeredCorrect, mCard!!.dayLastReviewed!!)
        mCard!!.dueDate = dueDate
        return updateDB { dataSource.updateCard(mCard!!)}
    }

    fun deleteCard(): Completable {
        return updateDB { dataSource.deleteCard(mCard!!) }
    }
}