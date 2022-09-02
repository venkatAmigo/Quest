package com.example.quest

import android.content.SharedPreferences

lateinit var Prefs: SharedPreferences

fun SharedPreferences.putAny(name:String,value:Any){
    when(value){
        is String -> edit().putString(name,value).apply()
        is Boolean -> edit().putBoolean(name,value).apply()
        is Int -> edit().putInt(name,value).apply()
    }
}

fun SharedPreferences.remove(name: String){
    edit().remove(name).apply()
}