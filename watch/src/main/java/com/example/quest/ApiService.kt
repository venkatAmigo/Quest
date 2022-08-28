package com.example.quest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    fun getInstance(): Api {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://wsk2019.mad.hakta.pro/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(Api::class.java)
    }

    suspend fun doLogin(loginBody: LoginBody): Response<Token> = withContext(Dispatchers.IO) {
        return@withContext getInstance().login(loginBody)
    }
}