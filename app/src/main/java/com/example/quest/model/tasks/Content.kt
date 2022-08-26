package com.example.quest.model.tasks

data class Content(
    val audios: List<String>,
    val completionTime: Int,
    val description: String,
    val endDateConstraint: String,
    val finishDate: String,
    val goalType: String,
    val goalValue: String,
    val id: String,
    val location: Location,
    val name: String,
    val photos: List<String>,
    val quest: Quest,
    val startDate: String,
    val startDateConstraint: String,
    val videos: List<String>
)