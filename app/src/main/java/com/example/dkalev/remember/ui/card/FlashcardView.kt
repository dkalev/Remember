package com.example.dkalev.remember.flashcard

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.Constraints
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.example.dkalev.remember.R

class FlashcardView : ConstraintLayout {
    private var mFront: android.support.v7.widget.CardView? = null
    private var mBack: android.support.v7.widget.CardView? = null
    private var leftIn: AnimatorSet? = null
    private var leftOut: AnimatorSet? = null
    private var rightIn: AnimatorSet? = null
    private var rightOut: AnimatorSet? = null

    var isFlipped: Boolean = false
        private set

    val side: Int
        get() = if (mFront!!.visibility == View.VISIBLE) {
            0
        } else {
            1
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

    private fun init(context: Context) {
        View.inflate(context, R.layout.fragment_card, this)
        isFlipped = false

        mFront = findViewById(R.id.card_view_front)
        mBack = findViewById(R.id.card_view_back)

        //get display metrics
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()
        display.getMetrics(dm)

        val scale = dm.density * 8000
        mFront!!.cameraDistance = scale
        mBack!!.cameraDistance = scale

        //make the card fill the whole screen
        findViewById<View>(R.id.card_layout).layoutParams = Constraints.LayoutParams(dm.widthPixels, dm.heightPixels)

        val minSide = Math.min(dm.widthPixels, dm.heightPixels)
        val cardSize = minSide - minSide / 10
        mFront!!.layoutParams.width = cardSize
        mFront!!.layoutParams.height = cardSize
        mBack!!.layoutParams.width = cardSize
        mBack!!.layoutParams.height = cardSize

        //add flip animation
        initFlipAnimation(context)

    }


    fun flipCard() {
        if (mFront!!.visibility == View.GONE) {
            leftIn!!.start()
            leftOut!!.start()
        } else if (mBack!!.visibility == View.GONE) {
            rightIn!!.start()
            rightOut!!.start()
            isFlipped = true
        }

    }

    fun reset() {
        if (mFront!!.visibility == View.GONE) {
            flipCard()
        }
        isFlipped = false
    }

    private fun initFlipAnimation(context: Context) {

        leftIn = AnimatorInflater.loadAnimator(context, R.animator.left_in_animation) as AnimatorSet
        leftOut = AnimatorInflater.loadAnimator(context, R.animator.left_out_animation) as AnimatorSet
        rightIn = AnimatorInflater.loadAnimator(context, R.animator.right_in_animation) as AnimatorSet
        rightOut = AnimatorInflater.loadAnimator(context, R.animator.right_out_animation) as AnimatorSet


        leftIn!!.setTarget(mFront)
        leftOut!!.setTarget(mBack)
        rightIn!!.setTarget(mBack)
        rightOut!!.setTarget(mFront)


        leftIn!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                mFront!!.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {
                mFront!!.visibility = View.GONE
            }
        })

        leftOut!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                mBack!!.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                mBack!!.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {
                mBack!!.visibility = View.VISIBLE
            }
        })

        rightIn!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                mBack!!.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {
                mBack!!.visibility = View.GONE
            }
        })
        rightOut!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                mFront!!.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                mFront!!.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {
                mFront!!.visibility = View.VISIBLE
            }
        })
    }
}