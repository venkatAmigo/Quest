package com.example.quest.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quest.model.Quest
import com.example.quest.model.Quests


@Dao
interface QuestDao {
    @Query("Select * from Quest")
    fun getAll():LiveData<List<Quests>>

    @Query("Select * from Quest where id= :id")
    fun getOne(id:Int):List<Quests>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(quest: Quests)

    @Query("Delete from Quest where id= :id")
    fun delete(id: Int):Int
}