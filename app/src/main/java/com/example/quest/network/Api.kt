package com.example.quest.network

import com.example.quest.model.*
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("user/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginBody: LoginBody): Response<Token>

    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun signup(@Body signupBody: SignupBody): Response<String>

    @GET("quests/popular")
    suspend fun getPopularQuests(@Header("Token") token : String): Response<QuestsList>

    @GET("quests")
    suspend fun getAllQuests(@Header("Token") token : String): Response<QuestsList>

    @GET("user/profile")
    suspend fun getUserDetails(@Header("Token") token : String): Response<Profile>

}