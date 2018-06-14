package com.example.dkalev.remember.deck;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DeckRecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector mGestureDetector;
    private ClickListener mClickListener;


    public DeckRecyclerTouchListener(Context context, final RecyclerView rv, final ClickListener clickListener){
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                DeckItemView child = (DeckItemView)rv.findChildViewUnder(e.getX(), e.getY());
                mClickListener.onClick(child);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                DeckItemView child = (DeckItemView) rv.findChildViewUnder(e.getX(), e.getY());

                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child);
                }
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY){
                DeckItemView child = (DeckItemView) rv.findChildViewUnder(e1.getX(), e1.getY());
                if (child != null && clickListener != null) {
                    clickListener.onFling(child, e1, e2, velocityX, velocityY);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface ClickListener {
        void onClick(DeckItemView view);

        void onLongClick(DeckItemView view);

        void onFling(DeckItemView view, MotionEvent e1, MotionEvent e2, float velocityX,
                     float velocityY);
    }
}
