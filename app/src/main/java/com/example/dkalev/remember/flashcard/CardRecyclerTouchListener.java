package com.example.dkalev.remember.flashcard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class CardRecyclerTouchListener implements RecyclerView.OnItemTouchListener  {

    private GestureDetector mGestureDetector;
    private ClickListener mClickListener;

    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    public static int SWIPE_OUT = 0;
    public static int SWIPE_IN = 1;


    public CardRecyclerTouchListener(Context context, final RecyclerView rv, final ClickListener clickListener){
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                FlashcardView child = (FlashcardView)rv.findChildViewUnder(e.getX(), e.getY());
                mClickListener.onClick(child);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                FlashcardView child = (FlashcardView) rv.findChildViewUnder(e.getX(), e.getY());

                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        FlashcardView v = (FlashcardView) rv.findChildViewUnder(e.getX(), e.getY());
        mGestureDetector.onTouchEvent(e);
        if (!v.isFlipped()) {
            return false;
        }

        final int action = e.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = e.getActionIndex();
                final float x = e.getX(pointerIndex);
                final float y = e.getY(pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = e.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        e.findPointerIndex(mActivePointerId);

                final float x = e.getX(pointerIndex);
                final float y = e.getY(pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                v.setTranslationX(v.getTranslationX() + dx);
                v.setTranslationY(v.getTranslationY() + dy);

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;

                if (v.getTranslationX() < -v.getWidth()/4 || v.getTranslationX() > v.getWidth()/4
                        || v.getTranslationY() < - v.getHeight()/4 || v.getTranslationY() > v.getHeight()/4){
                    mClickListener.onSwipe(v, SWIPE_OUT);
                }else{
                    mClickListener.onSwipe(v, SWIPE_IN);
                }

                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = e.getActionIndex();
                final int pointerId = e.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = e.getX(newPointerIndex);
                    mLastTouchY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface ClickListener {
        void onClick(FlashcardView view);

        void onLongClick(FlashcardView view);

        void onSwipe(FlashcardView view, int swipe_type);
    }
}
