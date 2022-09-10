package com.example.quest.utils

import android.os.Build.VERSION_CODES.M
import android.util.Log
import com.example.quest.model.*
import com.example.quest.utils.MainFunctions
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.LocalDate
import java.time.Period
import kotlin.math.ln
import kotlin.math.roundToInt


@RunWith(Parameterized::class)
class MainFunctionsTest (val questNo: Int, val taskNo: Int,val startDate: String, val endDate:
String, val status: String,val taskCompletionTime: Int, val expectedLevel: Int){

    lateinit var quests: MutableList<Quest>
    @Before
    fun setup(){
        quests = mutableListOf(Quest(
            1,
            "test1",
            "",
            listOf(""),
            "2022-08-09",
            "2022-08-10",
            "2022-08-20",
            "",
            50,
            category = QuestCategory(1,"","",""),
            tags =listOf(""),
            tasks = listOf(
                TaskListItem(1,"task1","COMPLETED","SECRET_KEY",100,"2022-08-24",
                    "2022-08-25"),TaskListItem(2,"task2","COMPLETED","SECRET_KEY",100,"2022-08-24",
                    "2022-08-25")
            )),Quest(
            2,
            "test1",
            "",
            listOf(""),
            "2022-08-09",
            "2022-08-10",
            "2022-08-20",
            "",
            50,
            category = QuestCategory(1,"","",""),
            tags =listOf(""),
            tasks = listOf(
                TaskListItem(1,"task1","COMPLETED","SECRET_KEY",100,"2022-08-24",
                    "2022-08-25"),
                TaskListItem(2,"task2","COMPLETED","SECRET_KEY",100,"2022-08-24",
                    "2022-08-26")
            ))
        )
    }

    companion object{
        @JvmStatic
        @Parameterized.Parameters
        fun data() : Collection<Array<Any>> {
            return listOf(
                arrayOf(0, 0,"2022-08-24","2022-08-26","COMPLETED",1000,3),
                arrayOf(0, 1,"2022-08-24","2022-08-28","COMPLETED",1000,2),
            )
        }
    }
    @Test
    fun calculateLevel() {
        quests[questNo].tasks?.get(taskNo)?.startDate =startDate
        quests[questNo].tasks?.get(taskNo)?.endDate =endDate
        quests[questNo].tasks?.get(taskNo)?.status =status
        quests[questNo].tasks?.get(taskNo)?.taskCompletionTime =taskCompletionTime
        val level = MainFunctions().calculateLevel(quests)
        assertEquals(expectedLevel,level)
    }
}