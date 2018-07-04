package com.example.dkalev.remember.deck

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class DeckRecyclerTouchListener(context: Context, rv: RecyclerView, private val mClickListener: ClickListener?) : RecyclerView.OnItemTouchListener {
    private val mGestureDetector: GestureDetector

    init {
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                mClickListener!!.onClick(child)
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = rv.findChildViewUnder(e.x, e.y)

                if (child != null && mClickListener != null) {
                    mClickListener.onLongClick(child)
                }
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                                 velocityY: Float): Boolean {
                val child = rv.findChildViewUnder(e1.x, e1.y)
                if (child != null && mClickListener != null) {
                    mClickListener.onFling(child, e1, e2, velocityX, velocityY)
                }
                return true
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

    interface ClickListener {
        fun onClick(view: View?)

        fun onLongClick(view: View?)

        fun onFling(view: View?, e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float)
    }
}