package com.example.dkalev.remember.ui.card

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.dkalev.remember.model.card.Card
import kotlinx.android.synthetic.main.fragment_card.view.*

class CardsAdapter (private val cards: List<Card>) : RecyclerView.Adapter<CardsAdapter.ViewHolder>(){

    var cardState: ArrayList<Int>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val textFront: TextView = itemView.cardFrontTextView
        internal val textBack: TextView = itemView.cardBackTextView
        internal val imageFront: ImageView = itemView.imageViewFront
        internal val imageBack: ImageView = itemView.imageViewBack
        internal val cardLayout: FlashcardView = itemView as FlashcardView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsAdapter.ViewHolder {
        Log.d("CardsAdapter", "flashcard viewholder created")
        val flashcardView = FlashcardView(parent.context)
        return ViewHolder(flashcardView)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardsAdapter.ViewHolder, position: Int) {
        val card = cards[position]
        holder.cardLayout.tag = position

        var tv = holder.textFront
        tv.text = card.textFront
        tv = holder.textBack
        tv.text = card.textBack
        if (cardState != null){
            Log.d("CardsAdapter", "cardState at $position is ${cardState!![position]}")
            if (cardState!![position] == 1){
                holder.cardLayout.side = 1
            }
        }
    }

    //reset card so it is ready for reuse
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.cardLayout.reset()
    }
}