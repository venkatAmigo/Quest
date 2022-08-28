package com.example.quest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.quest.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : FragmentActivity() {
    lateinit var binding: ActivityLoginBinding
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
                    Prefs.putAny("LOGIN",true)
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@LoginActivity, "response error ${response.errorBody()}", Toast
                        .LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}