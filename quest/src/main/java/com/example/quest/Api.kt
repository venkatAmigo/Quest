package com.example.quest

import android.provider.ContactsContract
import com.example.quest.model.QuestsList
import com.example.quest.model.TaskParticipant
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @GET("quests")
    suspend fun getPopularQuests(@Header("Token") token: String): Response<QuestsList>

    @POST("user/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginBody: LoginBody): Response<Token>

    //    tasks/2/participants
    @GET("tasks/2/participants")
    suspend fun getTaskParticipants(@Header("Token") token: String): Response<TaskParticipant>


}