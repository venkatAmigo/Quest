package com.example.quest

import android.app.Application
import android.content.Context
import com.example.quest.utils.Prefs

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs = getSharedPreferences(this.packageName, Context.MODE_PRIVATE)

    }
}