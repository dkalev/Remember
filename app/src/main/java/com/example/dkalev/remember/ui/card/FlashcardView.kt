package com.example.dkalev.remember.ui.card

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.constraint.Constraints
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.dkalev.remember.R
import kotlinx.android.synthetic.main.fragment_card.view.*

class FlashcardView : ConstraintLayout {
    private lateinit var leftIn: AnimatorSet
    private lateinit var leftOut: AnimatorSet
    private lateinit var rightIn: AnimatorSet
    private lateinit var rightOut: AnimatorSet

    var isFlipped: Boolean = false

    var side: Int
        get() = if (card_view_front.visibility == View.VISIBLE) {
            0
        } else {
            1
        }
        set(side) = if (side == 1) {
            card_view_front.visibility = View.GONE
            card_view_back.visibility = View.VISIBLE
        }else{
            card_view_front.visibility = View.VISIBLE
            card_view_back.visibility = View.GONE
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.d("FlashcardView", "saving instance state")
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isFlipped = isFlipped
        savedState.side = side
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d("FlashcardView", "restoring instance state")
        if (state is SavedState) {
            Log.d("FlashcardView", "is flipped: ${state.isFlipped}, " +
                    "side: ${state.side}")
            super.onRestoreInstanceState(state)
            isFlipped = state.isFlipped
            if(state.side != side)
                flipCard()
        }else{
            super.onRestoreInstanceState(state)
        }
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.fragment_card, this)
        isFlipped = false

        //get display metrics
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)

        val scale = dm.density * 8000
        card_view_front.cameraDistance = scale
        card_view_back.cameraDistance = scale

        //make the card fill the whole screen
        card_layout.layoutParams = Constraints.LayoutParams(dm.widthPixels, dm.heightPixels)

        val minSide = Math.min(dm.widthPixels, dm.heightPixels)
        val cardSize = minSide - minSide / 10
        card_view_front.layoutParams.width = cardSize
        card_view_front.layoutParams.height = cardSize
        card_view_back.layoutParams.width = cardSize
        card_view_back.layoutParams.height = cardSize

        //add flip animation
        initFlipAnimation(context)

    }


    fun flipCard() {
        if (card_view_front.visibility == View.GONE) {
            leftIn.start()
            leftOut.start()
        } else if (card_view_back.visibility == View.GONE) {
            rightIn.start()
            rightOut.start()
            isFlipped = true
        }

    }

    fun reset() {
        if (side == 1) {
            side = 0
        }
        isFlipped = false
    }

    private fun initFlipAnimation(context: Context) {

        leftIn = AnimatorInflater.loadAnimator(context, R.animator.left_in_animation) as AnimatorSet
        leftOut = AnimatorInflater.loadAnimator(context, R.animator.left_out_animation) as AnimatorSet
        rightIn = AnimatorInflater.loadAnimator(context, R.animator.right_in_animation) as AnimatorSet
        rightOut = AnimatorInflater.loadAnimator(context, R.animator.right_out_animation) as AnimatorSet


        leftIn.setTarget(card_view_front)
        leftOut.setTarget(card_view_back)
        rightIn.setTarget(card_view_back)
        rightOut.setTarget(card_view_front)

        val interpolator = AccelerateDecelerateInterpolator()
        leftIn.interpolator = interpolator
        leftOut.interpolator = interpolator
        rightIn.interpolator = interpolator
        rightOut.interpolator = interpolator

        leftIn.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                card_view_front.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {
                card_view_front.visibility = View.GONE
            }
        })

        leftOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                card_view_back.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                card_view_back.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {
                card_view_back.visibility = View.VISIBLE
            }
        })

        rightIn.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                card_view_back.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {
                card_view_back.visibility = View.GONE
            }
        })
        rightOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                card_view_front.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                card_view_front.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {
                card_view_front.visibility = View.VISIBLE
            }
        })
    }

    internal class SavedState : View.BaseSavedState {
        var isFlipped: Boolean = false
        var side: Int = 0

        constructor(source: Parcel): super(source){
            isFlipped = source.readByte().toInt() != 0
            side = source.readByte().toInt()
        }
        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(destination: Parcel, flags: Int) {
            super.writeToParcel(destination, flags)
            destination.writeByte((if (isFlipped) 1 else 0).toByte())
            destination.writeByte(side.toByte())
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}