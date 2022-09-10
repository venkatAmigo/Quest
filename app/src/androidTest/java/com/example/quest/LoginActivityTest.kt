package com.example.quest

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private lateinit var loginIdlingResource: CountingIdlingResource

    private var decorView: View? = null
    @Before
    fun setUp() {
        Intents.init()
        activityRule.scenario.onActivity{ activity ->
            loginIdlingResource = activity.loginIdlingResource
            IdlingRegistry.getInstance().register(loginIdlingResource)
            IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS)
            IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS)
            decorView = activity.window.decorView
        }
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(loginIdlingResource)
    }
    @Test
    fun testLogin(){
        onView(withId(R.id.email_et)).perform(typeText("ven@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_et)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.login_btn)).perform(click())
        intended(hasComponent(MainActivity::class.java.name));
    }
    @Test
    fun testLoginError(){
        onView(withId(R.id.email_et)).perform(typeText("vdfd@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.password_et)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.login_btn)).perform(click())
        onView(withText("Invalid Credentials")).check(matches(isDisplayed()))
    }
}