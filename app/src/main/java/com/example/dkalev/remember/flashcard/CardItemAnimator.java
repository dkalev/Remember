package com.example.dkalev.remember.flashcard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class CardItemAnimator extends DefaultItemAnimator {

    private float mViewX;
    private float mViewY;
    private static final String DEBUG_TAG = "CardItemAnimator";

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        mViewX = viewHolder.itemView.getX();
        mViewY = viewHolder.itemView.getY();
//        Log.d(DEBUG_TAG, mViewX + ", " + mViewY);
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        Log.d(DEBUG_TAG, "PreLayout" + preLayoutInfo.left + ", " + preLayoutInfo.right
                + ", " + preLayoutInfo.top+ ", " + preLayoutInfo.bottom);
        Log.d(DEBUG_TAG, "PostLayout" + postLayoutInfo.left + ", " + postLayoutInfo.right
                + ", " + postLayoutInfo.top+ ", " + postLayoutInfo.bottom);

        return super.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo);
    }


    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        Log.d(DEBUG_TAG, "viewHolder" + viewHolder.itemView.getLeft() + ", " + viewHolder.itemView.getRight()
                + ", " + viewHolder.itemView.getTop()+ ", " + viewHolder.itemView.getBottom());
        return super.recordPostLayoutInformation(state, viewHolder);
    }
}
