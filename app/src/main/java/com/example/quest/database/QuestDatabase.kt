package com.example.quest.database

import android.content.Context
import androidx.room.*
import com.example.quest.model.Quest
import com.example.quest.model.Quests

@Database(entities = [Quests::class], version = 3, exportSchema = false)
@TypeConverters(StringConverter::class,CategoryConverter::class,TaskConverter::class)
abstract class QuestDatabase : RoomDatabase() {
    abstract fun getDao():QuestDao

    companion object{
        @Volatile
        var instance:QuestDatabase?=null
        fun getDatabaseInstance(context: Context):QuestDatabase{
            if(instance!=null)
                return instance as QuestDatabase
            synchronized(this){
                instance = Room.databaseBuilder(context,QuestDatabase::class.java,"quests.db").build()
                return instance as QuestDatabase
            }

        }
    }
}