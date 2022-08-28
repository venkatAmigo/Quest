package com.example.quest.model

data class Task(
    val endDate: String,
    val id: String,
    val name: String,
    val startDate: String,
    val status: String,
    val taskCompletionTime: String
)