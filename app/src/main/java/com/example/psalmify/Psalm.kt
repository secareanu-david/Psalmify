package com.example.psalmify
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "psalm_table")
data class Psalm(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "content") val content: String
)