package com.example.quest.model

data class WeatherInfo (
    val temperature: Double,
    val pressure: Int,
    val humidity: String,
    val windSpeed: Double,
    val windDegree: String
) {
}
