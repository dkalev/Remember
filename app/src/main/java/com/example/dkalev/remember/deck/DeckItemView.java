package com.example.dkalev.remember.deck;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.example.dkalev.remember.R;

public class DeckItemView extends ConstraintLayout {
    public DeckItemView(Context context) {
        super(context);
        init(context);
    }

    public DeckItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DeckItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.deck_item, this);
    }

}
