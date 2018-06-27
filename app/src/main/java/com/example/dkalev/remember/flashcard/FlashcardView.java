package com.example.dkalev.remember.flashcard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.example.dkalev.remember.R;

public class FlashcardView extends ConstraintLayout {
    private android.support.v7.widget.CardView mFront;
    private android.support.v7.widget.CardView mBack;
    private AnimatorSet mLeft_in;
    private AnimatorSet mLeft_out;
    private AnimatorSet mRight_in;
    private AnimatorSet mRight_out;

    private boolean flipped;

    public FlashcardView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public FlashcardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlashcardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.fragment_card, this);
        flipped = false;

        mFront = findViewById(R.id.card_view_front);
        mBack = findViewById(R.id.card_view_back);

        //get display metrics
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        final float scale = dm.density*8000;
        mFront.setCameraDistance(scale);
        mBack.setCameraDistance(scale);

        //make the card fill the whole screen
        findViewById(R.id.card_layout).setLayoutParams(new Constraints.LayoutParams(dm.widthPixels, dm.heightPixels));

        int minSide = Math.min(dm.widthPixels, dm.heightPixels);
        int cardSize = minSide - minSide/10;
        mFront.getLayoutParams().width = cardSize;
        mFront.getLayoutParams().height = cardSize;
        mBack.getLayoutParams().width = cardSize;
        mBack.getLayoutParams().height = cardSize;

        //add flip animation
        initFlipAnimation(context);

    }


    public void flipCard(){
        if (mFront.getVisibility() == View.GONE) {
            mLeft_in.start();
            mLeft_out.start();
        } else if (mBack.getVisibility() == View.GONE){
            mRight_in.start();
            mRight_out.start();
            flipped = true;
        }

    }

    public boolean isFlipped(){
        return flipped;
    }

    public void reset(){
        if(mFront.getVisibility() == View.GONE) {
            flipCard();
        }
        flipped = false;
    }

    public int getSide(){
        if(mFront.getVisibility() == View.VISIBLE){
            return 0;
        }else{
            return 1;
        }
    }

    private void initFlipAnimation(Context context){

        mLeft_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.left_in_animation);
        mLeft_out = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.left_out_animation);
        mRight_in = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.right_in_animation);
        mRight_out = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.right_out_animation);


        mLeft_in.setTarget(mFront);
        mLeft_out.setTarget(mBack);
        mRight_in.setTarget(mBack);
        mRight_out.setTarget(mFront);


        mLeft_in.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFront.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                mFront.setVisibility(View.GONE);
            }
        });

        mLeft_out.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                mBack.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBack.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mBack.setVisibility(View.VISIBLE);
            }
        });

        mRight_in.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBack.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                mBack.setVisibility(View.GONE);
            }
        });
        mRight_out.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                mFront.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mFront.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mFront.setVisibility(View.VISIBLE);
            }
        });
    }
}
