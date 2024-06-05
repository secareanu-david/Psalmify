package com.example.psalmify

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromList(numbers: List<Int>): String {
        return Gson().toJson(numbers)
    }

    @TypeConverter
    fun toList(data: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(data, listType)
    }
}