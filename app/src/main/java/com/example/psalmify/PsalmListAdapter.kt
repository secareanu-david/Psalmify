package com.example.psalmify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PsalmListAdapter(
    private val context: Context,
    //private val psalmViewModel: HomeViewModel,
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

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val userDao = AppDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO)).userDao()
                    val user = userDao.getUser(currentUserId)
                    if (user != null) {
                        val favoritePsalmsList = user.getFavoritePsalmsList().toMutableList()
                        if (current.isFavorite)
                            favoritePsalmsList.add(current.psalm.id)
                        else
                            favoritePsalmsList.remove(current.psalm.id)

                        val updatedFavorites = user.setFavoritePsalmsList(favoritePsalmsList)
                        //modified list in room database
                        userDao.updateFavoritePsalms(currentUserId, updatedFavorites)

                        //modified list in firebase database
                        val db = Firebase.firestore
                        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                            val userFavoritesUpdate = mapOf("favoritePsalms" to updatedFavorites)

                            db.collection("users").document(currentUser.uid)
                                .update(userFavoritesUpdate)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Favorites Updated", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Error! ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }
            }

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
