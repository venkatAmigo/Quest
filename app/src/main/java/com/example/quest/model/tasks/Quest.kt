package com.example.quest.model.tasks

data class Quest(
    val authorName: String,
    val category: Category,
    val creationDate: String,
    val description: String,
    val difficulty: Int,
    val endDate: String,
    val id: Int,
    val mainPhoto: String,
    val name: String,
    val photos: List<String>,
    val rating: Int,
    val startDate: String,
    val tags: List<String>,
    val tasks: List<Task>
)