package com.example.dkalev.remember.deck;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.dkalev.remember.R;

import java.util.List;


public class DecksAdapter extends RecyclerView.Adapter<DecksAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mDeckName;
        private TextView mNumCards;

        public ViewHolder(View itemView) {
            super(itemView);
            mDeckName = itemView.findViewById(R.id.deckNameTextView);
            mNumCards = itemView.findViewById(R.id.numCardsTextView);
        }
    }

    private List<Deck> mDecks;

    public DecksAdapter(List<Deck> decks){
        mDecks = decks;
    }

    @NonNull
    @Override
    public DecksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        DeckItemView view = new DeckItemView(parent.getContext());
        return new DecksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deck deck = mDecks.get(position);

        TextView tv = holder.mDeckName;
        tv.setText(deck.getDeckName());
        tv = holder.mNumCards;
        tv.setText(String.valueOf(deck.getNumCards()));
    }

    @Override
    public int getItemCount() {
        return mDecks.size();
    }
}
