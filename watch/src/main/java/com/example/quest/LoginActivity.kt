package com.example.quest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.quest.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : Activity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                val response = ApiService.doLogin(LoginBody(email,password))
                if(response.code() == 200 && response.body() != null){
                    val token  = response.body() as Token
                    startActivity(Intent(this@LoginActivity,TaskActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this@LoginActivity,LoginErrorActivity::class.java))
                    finish()
                }
            }
        }

    }
}