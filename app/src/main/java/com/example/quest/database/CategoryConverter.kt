package com.example.quest.database

import androidx.room.TypeConverter
import com.example.quest.model.QuestCategory

class CategoryConverter {

    @TypeConverter
    fun fromCategory(category: QuestCategory): String {
        return "${category.id},${category.name},${category.description},${category.photo}"
    }

    @TypeConverter
    fun fromString(value: String?): QuestCategory? {
        val arr = value?.split(",")
        return arr?.get(0)?.let { QuestCategory(it.toInt(),arr[1],arr[2],arr[3]) }
    }
}