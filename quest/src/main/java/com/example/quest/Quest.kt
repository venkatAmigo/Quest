package com.example.quest

import java.io.Serializable


data class Quest(val id: Int,
    val name: String,
    val description: String? = null,
    val photos: List<String>? = null,
    val creationDate: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val mainPhoto: String? = null,
    val difficulty: Int? = null,
    val authorName: String? = null,
    val category: QuestCategory? = null,
    val tags: List<String>? = null,
    val tasks: List<TaskListItem>? = null
) : Serializable {
}

data class QuestCategory(
    val id: Int,
    val name: String,
    val description: String,
    val photo: String
) : Serializable {
}

data class TaskListItem(
    val id: Int,
    val name: String,
    val status: String,
    val goalType: String,
    val taskCompletionTime: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null
) : Serializable {}

