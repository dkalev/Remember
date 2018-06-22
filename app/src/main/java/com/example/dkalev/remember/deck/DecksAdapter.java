package com.example.dkalev.remember.deck;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dkalev.remember.R;
import com.example.dkalev.remember.model.Deck;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DecksAdapter extends RecyclerView.Adapter<DecksAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.deckNameTextView) TextView mDeckName;
        @BindView(R.id.numCardsTextView) TextView mNumCards;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private List<Deck> mDecks;

    public void setDeckList(final List<Deck> decks) {
        if (mDecks == null) {
            mDecks = decks;
            notifyItemRangeInserted(0, decks.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mDecks.size();
                }

                @Override
                public int getNewListSize() {
                    return decks.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mDecks.get(oldItemPosition).getDeckId() ==
                            decks.get(newItemPosition).getDeckId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Deck newDeck = decks.get(newItemPosition);
                    Deck oldDeck = mDecks.get(oldItemPosition);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        return newDeck.getDeckId() == oldDeck.getDeckId()
                                && Objects.equals(newDeck.getName(), oldDeck.getName())
                                && Objects.equals(newDeck.getCards(), oldDeck.getCards())
                                && newDeck.getSize() == oldDeck.getSize();
                    }
                    //if sdk is below kitkat return that nothing is the same => update the whole list
                    return false;
                }
            });
            mDecks = decks;
            result.dispatchUpdatesTo(this);
        }
    }

    public DecksAdapter(){

    }

    @NonNull
    @Override
    public DecksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View deckItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_item, parent, false);
        return new DecksAdapter.ViewHolder(deckItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deck deck = mDecks.get(position);

        //add the deck id to the view
        holder.itemView.setTag(deck.getDeckId());

        TextView tv = holder.mDeckName;
        tv.setText(deck.getName());
        tv = holder.mNumCards;
        tv.setText(String.valueOf(deck.getSize()));
    }

    @Override
    public int getItemCount() {
        return mDecks == null ? 0 : mDecks.size();
    }
}
