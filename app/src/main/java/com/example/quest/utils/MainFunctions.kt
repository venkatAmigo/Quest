package com.example.quest.utils

import android.util.Log
import com.example.quest.model.Quest
import java.time.LocalDate
import java.time.Period
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.roundToInt

class MainFunctions {
    fun calculateLevel(quests: List<Quest>) :Int {
        var questPoints= 0.0
        quests.forEach {
            var tasksPoints =0.0
            it.tasks?.forEach { task ->
                if(task.status == "COMPLETED"){
                    val completionTime = task.taskCompletionTime?.toDouble()
                    val minutes = Period.between(
                        LocalDate.parse(task.startDate),
                        LocalDate.parse(task.endDate)).days*24*60
                    tasksPoints += completionTime?.div(minutes.toDouble())!!
                }
            }
            questPoints += it.difficulty?.times(tasksPoints) ?: 0.0

        }
        System.out.println("points"+questPoints)
        return (ln((questPoints/5)+1) +1).roundToInt()

    }
}