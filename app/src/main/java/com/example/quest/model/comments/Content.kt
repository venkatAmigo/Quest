package com.example.quest.model.comments

data class Content(
    val author: Author,
    val date: String,
    val id: Int,
    val rating: Int,
    val text: String
)