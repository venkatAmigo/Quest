package com.example.quest.model

import java.io.Serializable

data class Task(
    val endDate: String,
    val id: String,
    val name: String,
    val startDate: String,
    val status: String,
    val taskCompletionTime: String
): Serializable{

}