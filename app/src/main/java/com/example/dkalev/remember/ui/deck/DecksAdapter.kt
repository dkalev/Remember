package com.example.dkalev.remember.deck

import android.os.Build
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dkalev.remember.R
import com.example.dkalev.remember.model.deck.Deck
import kotlinx.android.synthetic.main.deck_item.view.*

class DecksAdapter : RecyclerView.Adapter<DecksAdapter.ViewHolder>() {

    private var mDecks: List<Deck>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        internal var deckNameTV = itemView.deckNameTextView
        internal var numCardsTV = itemView.numCardsTextView

    }

    fun setDeckList(decks: List<Deck>) {
        if (mDecks == null) {
            mDecks = decks
            notifyItemRangeInserted(0, decks.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mDecks!!.size
                }

                override fun getNewListSize(): Int {
                    return decks.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mDecks!![oldItemPosition].name === decks[newItemPosition].name
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val newDeck = decks[newItemPosition]
                    val oldDeck = mDecks!![oldItemPosition]
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        (newDeck.name === oldDeck.name
                                && newDeck.name == oldDeck.name
                                && newDeck.cards == oldDeck.cards
                                && newDeck.size == oldDeck.size)
                    } else false
                    //if sdk is below kitkat return that nothing is the same => update the whole list
                }
            })
            mDecks = decks
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val deckItemView = LayoutInflater.from(parent.context).inflate(R.layout.deck_item, parent, false)
        return ViewHolder(deckItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deck = mDecks!![position]

        //add the deck id to the view
        holder.itemView.tag = position
        
        var tv = holder.deckNameTV
        tv!!.text = deck.name
        tv = holder.numCardsTV
        tv!!.text = deck.size.toString()
    }

    override fun getItemCount(): Int {
        return if (mDecks == null) 0 else mDecks!!.size
    }
}
