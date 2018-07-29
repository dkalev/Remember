package com.example.dkalev.remember.srs

import java.util.*

object CardReviewScheduler {

    fun scheduleCard(difficultyRating: Double, answeredCorrect: Boolean, dateLastReviewed: Date): Date{
        val now = getCurrentDate()
        val daysSinceLastReview = daysBetween(now, dateLastReviewed)

        val percentOverdue =  if (answeredCorrect)
            Math.min(2, daysBetween(now , dateLastReviewed)/daysSinceLastReview)
        else 1

        val difficulty = betweenZeroOne(difficultyRating +  percentOverdue * (8 - 9 * difficultyRating) / 17)

        val difficultyWeight = 3 - 1.7 * difficulty

        val nextReview : Long = if (answeredCorrect){
            daysSinceLastReview * (1 + (difficultyWeight - 1) * percentOverdue).toLong()
        }else{
            1 / Math.pow(difficultyWeight, 2.0).toLong()
        }
        return Date(now.addDaysToDate(Math.min(1, nextReview)))
    }

    private fun daysBetween(date1: Date, date2: Date):Long{
        val diff = millisecondToDay(date1.time - date2.time)
        return if (diff > 0) diff else -diff
    }

    private fun getCurrentDate(): Date{
        return Calendar.getInstance().time
    }

    private fun betweenZeroOne(num: Double):Double{
        if (num > 1.0) return 1.0
        if (num < 0.0) return 0.0
        return num
    }

    private fun dayToMillisecond(day: Long):Long{
        return day*1000*60*60*24
    }

    private fun millisecondToDay(ms: Long):Long{
        return ms/(1000*60*60*24)
    }

    private fun Date.addDaysToDate(days: Long): Long{
        return this.time + dayToMillisecond(days)
    }
}