package com.example.quest.database

import androidx.room.TypeConverter
import com.example.quest.model.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class StringConverter {

    @TypeConverter
    fun fromPhoto(photo: Photo): String {
        // val list: List<String?>?
        val gson = Gson()
        return gson.toJson(photo.strings)
    }

    @TypeConverter
    fun fromString(value: String?): Photo {
        val listType = object :
            TypeToken<ArrayList<String?>?>() {}.type
        return Photo(Gson().fromJson(value, listType))
    }
}