package com.example.quest.network

import com.example.quest.model.LoginBody
import com.example.quest.model.Profile
import com.example.quest.model.QuestsList
import com.example.quest.model.Token
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("user/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginBody: LoginBody): Response<Token>

    @GET("quests/popular")
    suspend fun getPopularQuests(@Header("Token") token : String): Response<QuestsList>

    @GET("user/profile")
    suspend fun getUserDetails(@Header("Token") token : String): Response<Profile>

}