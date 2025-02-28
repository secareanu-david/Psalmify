package com.example.psalmify

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "favorite_psalms") val favoritePsalms: String
) {

    fun getFavoritePsalmsList(): List<Int> {
        return favoritePsalms.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun setFavoritePsalmsList(favorites: List<Int>): String {
        return favorites.joinToString(",")
    }
}