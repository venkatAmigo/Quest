package com.example.quest

import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("user/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginBody: LoginBody): Response<Token>

}