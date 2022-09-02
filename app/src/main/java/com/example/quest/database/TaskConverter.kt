package com.example.quest.database

import androidx.room.TypeConverter
import com.example.quest.model.DummyTask
import com.example.quest.model.QuestCategory
import com.example.quest.model.TaskListItem

class TaskConverter {
    @TypeConverter
        fun fromCategory(categorys: DummyTask): String {
        var tos = ""

        categorys.tasks.forEach { category->
            tos+= "${category.id},${category.name},${category.status},${category.goalType}" +
                    ",${category.taskCompletionTime},${category.startDate},${category.endDate}"
            tos+="-"
        }
        tos.removeRange(tos.length-1,tos.length-1)
            return tos
        }

        @TypeConverter
        fun fromString(value: String?): DummyTask {
            val ar = value?.split("-")
            var taskItems = mutableListOf<TaskListItem>()
            ar?.forEach {
                val arr = it.split(",")
                if(arr.size == 7)
                arr.get(0).let { TaskListItem(it.toInt(),arr[1],arr[2],arr[3],arr[4]
                    .toInt(),
                   arr[5],arr[6]) }.let { it1 -> taskItems.add(it1) }

            }
            return DummyTask(taskItems)
        }
}