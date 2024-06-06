package com.example.psalmify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PsalmListAdapter(
    private val psalmViewModel: HomeViewModel,
    private val listener: RecyclerViewEvent
) : ListAdapter<PsalmItem, PsalmListAdapter.ItemViewHolder>(PSALMS_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(parent, listener)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.psalm.id, current.psalm.description, current.isFavorite)

        //TODO Favorite/Unfavorite for backend
        holder.imageButtonFavorite.setOnClickListener {
            current.isFavorite = !current.isFavorite
            holder.imageButtonFavorite.isSelected = current.isFavorite
        }
    }
    class ItemViewHolder(view: View, private val listener: RecyclerViewEvent) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val textViewPsalmNumber: TextView = view.findViewById(R.id.textViewPsalmNumber)
        private val textViewPsalmDescription: TextView = view.findViewById(R.id.textViewPsalmDescription)
        val imageButtonFavorite: ImageButton = view.findViewById(R.id.imageButtonFavorite)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(adapterPosition)
            }
        }

        fun bind(psalmNumber : Int, descriptionText : String?, isFavorite : Boolean){
            textViewPsalmNumber.text = psalmNumber.toString()
            textViewPsalmDescription.text = descriptionText
            imageButtonFavorite.isSelected = isFavorite
        }
        companion object {
            fun create(parent: ViewGroup, listener: RecyclerViewEvent): ItemViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_psalm_item, parent, false)
                return ItemViewHolder(view, listener)
            }
        }
    }
    companion object {
        private val PSALMS_COMPARATOR = object : DiffUtil.ItemCallback<PsalmItem>() {
            override fun areItemsTheSame(oldItem: PsalmItem, newItem: PsalmItem): Boolean {
                return oldItem === newItem
            }
            override fun areContentsTheSame(oldItem: PsalmItem, newItem: PsalmItem): Boolean {
                return oldItem.psalm == newItem.psalm
            }
        }
    }
    interface RecyclerViewEvent {
        fun onItemClicked(position : Int)
    }
}
