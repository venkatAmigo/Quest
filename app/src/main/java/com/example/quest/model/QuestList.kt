package com.example.quest.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.quest.database.CategoryConverter
import com.example.quest.database.StringConverter
import java.io.Serializable

data class QuestsList(
    val meta: List<ListMeta>,
    val content: List<Quest>,

):Serializable {
}
data class ListMeta(
    val totalPages: Int,
    val totalItems: Int,
    val currentPage: Int
) :Serializable{
}
@Entity(tableName = "Quest")
data class Quests(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String? = null,
    @TypeConverters(StringConverter::class)
    val photos: Photo? = null,
    val creationDate: String? = null,
    val startDate:  String? = null,
    val endDate:  String? = null,
    val mainPhoto: String? = null,
    val difficulty: Int? = null,
    @TypeConverters(CategoryConverter::class)
    val category: QuestCategory? = null,
    @TypeConverters(StringConverter::class)
    val tags: Photo? = null,
    @TypeConverters(CategoryConverter::class)
    val tasks: DummyTask? = null
) :Serializable{
}
data class Quest(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String? = null,
    val photos: List<String>? = null,
    val creationDate: String? = null,
    val startDate:  String? = null,
    val endDate:  String? = null,
    val mainPhoto: String? = null,
    val difficulty: Int? = null,
    val authorName: String? = null,
    val category: QuestCategory? = null,
    val tags: List<String>? = null,
    val tasks: List<TaskListItem>? = null
) :Serializable{
}

data class Photo(val strings: List<String>)
data class DummyTask(val tasks: List<TaskListItem>)
@Entity(tableName = "QuestCategory")
data class QuestCategory(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val photo: String
) :Serializable{
}
@Entity(tableName = "TaskListItem")
data class TaskListItem(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val goalType: String,
    val taskCompletionTime: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null
):Serializable {
}