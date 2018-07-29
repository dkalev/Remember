package com.example.dkalev.remember.ui.card

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID

class CardRecyclerTouchListener(context: Context, rv: RecyclerView, private val clickListener: ClickListener?) : RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector

    private var lastTouchX: Float = 0.toFloat()
    private var lastTouchY: Float = 0.toFloat()
    private var activePointerId = INVALID_POINTER_ID
    private lateinit var cardView: FlashcardView
    private var isSwiping: Boolean = false
    private var viewInitialized: Boolean = false


    init {
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y) as FlashcardView
                clickListener!!.onClick(child)
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = rv.findChildViewUnder(e.x, e.y) as FlashcardView

                clickListener?.onLongClick(child)
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        mGestureDetector.onTouchEvent(e)

        if (!viewInitialized) {
            cardView = rv.findChildViewUnder(e.x, e.y) as FlashcardView
            viewInitialized = true
        }

        if (!cardView.isFlipped) {
            return true
        }

        val action = e.actionMasked
        if (action == MotionEvent.ACTION_DOWN){
            val pointerIndex = e.actionIndex
            val x = e.getX(pointerIndex)
            val y = e.getY(pointerIndex)

            // Remember where we started (for dragging)
            lastTouchX = x
            lastTouchY = y
            // Save the ID of this pointer (for dragging)
            activePointerId = e.getPointerId(0)

            isSwiping = true
        }
        return true
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        mGestureDetector.onTouchEvent(e)
        if (isSwiping){

            val action = e.actionMasked

            when (action) {
                MotionEvent.ACTION_MOVE -> {
                    // Find the index of the active pointer and fetch its position
                    val pointerIndex = e.findPointerIndex(activePointerId)

                    val x = e.getX(pointerIndex)
                    val y = e.getY(pointerIndex)

                    // Calculate the distance moved
                    val dx = x - lastTouchX
                    val dy = y - lastTouchY

                    cardView.translationX = cardView.translationX + dx
                    cardView.translationY = cardView.translationY + dy

                    // Remember this touch position for the next move event
                    lastTouchX = x
                    lastTouchY = y
                }

                MotionEvent.ACTION_UP -> {
                    activePointerId = INVALID_POINTER_ID
                    isSwiping = false
                    viewInitialized = false

                    if (cardView!!.translationX < -cardView.width / 4 || cardView.translationX > cardView.width / 4
                            || cardView.translationY < -cardView.height / 4 || cardView.translationY > cardView.height / 4) {
                        clickListener!!.onSwipe(cardView, SWIPE_OUT)
                    } else {
                        clickListener!!.onSwipe(cardView, SWIPE_IN)
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    activePointerId = INVALID_POINTER_ID
                    isSwiping = false
                    viewInitialized = false
                }

                MotionEvent.ACTION_POINTER_UP -> {

                    val pointerIndex = e.actionIndex
                    val pointerId = e.getPointerId(pointerIndex)

                    if (pointerId == activePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        lastTouchX = e.getX(newPointerIndex)
                        lastTouchY = e.getY(newPointerIndex)
                        activePointerId = e.getPointerId(newPointerIndex)
                    }
                }
            }
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    interface ClickListener {
        fun onClick(view: FlashcardView)

        fun onLongClick(view: FlashcardView?)

        fun onSwipe(view: FlashcardView, swipe_type: Int)
    }

    companion object {
        var SWIPE_OUT = 0
        var SWIPE_IN = 1
    }
}