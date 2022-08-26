package com.example.quest

import android.content.Intent
import android.graphics.Color.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.quest.utils.Prefs
import com.example.quest.utils.putAny
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroFragment

class OnboardActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setImmersiveMode()
        setIndicatorColor(getColor(R.color.green),getColor(R.color.white))
        addSlide(
            AppIntroCustomLayoutFragment.newInstance(R.layout.slider_one))
        addSlide(
            AppIntroCustomLayoutFragment.newInstance(R.layout.slider_one))
        addSlide(
            AppIntroCustomLayoutFragment.newInstance(R.layout.slider_one))

    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        Prefs.putAny("IS_ONBOARD_SHOWN",true)
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        Prefs.putAny("IS_ONBOARD_SHOWN",true)
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }
}