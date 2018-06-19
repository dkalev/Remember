package com.example.dkalev.remember.flashcard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.model.Card;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final String DEBUG_TAG = "CardsAdapter";

        private TextView mTextFront;
        private TextView mTextBack;
        private ImageView mImageFront;
        private ImageView mImageBack;
        private FlashcardView mCardLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mCardLayout = (FlashcardView) itemView;
            mTextFront = itemView.findViewById(R.id.cardFrontTextView);
            mTextBack = itemView.findViewById(R.id.cardBackTextView);
            mImageFront = itemView.findViewById(R.id.imageViewFront);
            mImageBack = itemView.findViewById(R.id.imageViewBack);
        }
    }

    private List<Card> mCards;

    public CardsAdapter(List<Card> cards){
        mCards = cards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FlashcardView flashcardView = new FlashcardView(parent.getContext());
        return new ViewHolder(flashcardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Card card = mCards.get(position);

        TextView tv = holder.mTextFront;
        tv.setText(card.getTextFront());
        tv = holder.mTextBack;
        tv.setText(card.getTextBack());

    }

    //reset the card so it is ready for reuse
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder){
        super.onViewRecycled(holder);
        holder.mCardLayout.reset();
    }


    @Override
    public int getItemCount() {
        return mCards.size();
    }
}
