package com.example.quest.model

import java.io.Serializable

data class Category(
    val description: String,
    val id: Int,
    val name: String,
    val photo: String
): Serializable{

}