package com.example.quest.utils

import android.os.Build.VERSION_CODES.M
import android.util.Log
import com.example.quest.model.*
import com.example.quest.utils.MainFunctions
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runners.Parameterized
import java.time.LocalDate
import java.time.Period
import kotlin.math.ln
import kotlin.math.roundToInt

class MainFunctionsTest {

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
                    "2022-08-25"),TaskListItem(2,"task2","COMPLETED","SECRET_KEY",100,"2022-08-24",
                    "2022-08-25")
            ))
        )
    }
    @Test
    fun calculateLevel(value: Int) {
       //Added two quests of containing 2 completed tasks each
        // quests.add(Quest()) : new Quest can be added like this

        //To change values of any task in Quest
      /*  quests[0].tasks?.get(0)?.status ="IN_PROGRESS"
        quests[0].tasks?.get(0)?.startDate ="2022-08-24"
        quests[0].tasks?.get(0)?.endDate ="2022-08-24"*/
        val level = MainFunctions().calculateLevel(quests)
        assertEquals(2,level)
    }
}