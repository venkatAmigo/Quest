package com.example.quest.model

data class SignupBody(
    val email: String,
    val nickName: String,
    val password: String,
    val phone: String
)