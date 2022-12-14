package com.example.quest

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.quest.databinding.ActivityLoginBinding
import com.example.quest.model.LoginBody
import com.example.quest.model.Token
import com.example.quest.network.ApiService
import com.example.quest.utils.AlertHelper
import com.example.quest.utils.Prefs
import com.example.quest.utils.putAny
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var callbackManager: CallbackManager

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val loginIdlingResource = CountingIdlingResource("login")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(BuildConfig.DEBUG){
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            loginIdlingResource.increment()
            CoroutineScope(Dispatchers.Main).launch {
                Log.i("B32",LoginBody(email,password).toString())
                val response = ApiService.doLogin(LoginBody(email,password))

                if(response.code() == 200 && response.body() != null){
                    val token  = response.body() as Token
                    Prefs.putAny("TOKEN",token.token)
                    Prefs.putAny("LOGIN",true)
                   loginIdlingResource.decrement()
                    Toast.makeText(this@LoginActivity, "You are successfully logged in", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }else{
                    val builder = AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Error")
                        .setMessage("Invalid Credentials")

                    val alert = builder.create()
                    alert.setButton(AlertDialog.BUTTON_POSITIVE,"OK"){ dialog, _ ->
                        alert.dismiss()

                    }
                    alert.show()
                    loginIdlingResource.decrement()
                    Log.i("B32",response.body().toString())
                }
            }
        }
        binding.signupBtn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.registerCallback(callbackManager,object:FacebookCallback<LoginResult>{
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }

            override fun onSuccess(result: LoginResult) {
                CoroutineScope(Dispatchers.Main).launch {
                    val response = ApiService.doLogin(LoginBody("ven@gmail.com",result
                        .accessToken.toString()))
                    if(response.code() == 200 && response.body() != null){
                        val toke  = response.body() as Token
                        Prefs.putAny("TOKEN",toke.token)
                        Prefs.putAny("LOGIN",true)
                        Log.i("token", Prefs.getBoolean("LOGIN",false).toString())
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "${response.errorBody()?.charStream()
                            ?.readText()}", Toast
                            .LENGTH_SHORT).show()
                    }
                }

            }

        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }
}