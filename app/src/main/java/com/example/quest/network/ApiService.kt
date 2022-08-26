package com.example.quest.network

import android.util.Log
import com.example.quest.model.*
import com.google.gson.Gson
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

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

    suspend fun getPopularQuests(token: String): Response<QuestsList> = withContext(
        Dispatchers
            .IO
    ) {
        return@withContext getInstance().getPopularQuests(token)
    }
    suspend fun getUserProfile(token: String): Response<Profile> = withContext(
        Dispatchers
            .IO
    ) {
        return@withContext getInstance().getUserDetails(token)
    }

    fun getWeather(token: String, city:String): WeatherInfo {
        val url = URL("http://wsk2019.mad.hakta.pro/api/weather?city=$city")
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("Token",token)
        con.setRequestProperty("accept", "application/xml")
        con.doInput = true
        con.doOutput = false
        val resCode = con.responseCode
        return if(resCode == HttpURLConnection.HTTP_OK) {
            val response = con.inputStream.bufferedReader(StandardCharsets.UTF_8).use {
                it.readText()
            }
            val res = XmlToJson.Builder(response)
                .skipTag("/WeatherInfo/content").build()
            val json = res.toString()
            val jsonObj = JSONObject(json)
            val we =jsonObj.getJSONObject("WeatherInfo")

            val gson = Gson().fromJson(we.toString(), WeatherInfo::class.java)
            Log.i("NOTS",json)
            gson
        }else{
            WeatherInfo(0.0,0,"",0.0,"")
        }
    }
}