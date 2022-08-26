package com.example.quest

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import com.example.quest.utils.Prefs

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        }

        val isLogin = Prefs.getBoolean("LOGIN",false)
        val accessToken = Prefs.getString("TOKEN","")
        accessToken?.let { Log.i("token", it) }
       Log.i("token", "hell"+isLogin)
        val isOnboardShown = Prefs.getBoolean("IS_ONBOARD_SHOWN",false)
        Handler(mainLooper).postDelayed({
            if(!isOnboardShown){
                startActivity(Intent(this,OnboardActivity::class.java))
                // finish()
            }
            else if (isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
               // finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                // finish()
            }
        }, 2000)
    }
}