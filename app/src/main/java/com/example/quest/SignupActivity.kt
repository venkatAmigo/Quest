package com.example.quest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.quest.databinding.ActivityLoginBinding
import com.example.quest.databinding.ActivitySignupBinding
import com.example.quest.model.LoginBody
import com.example.quest.model.SignupBody
import com.example.quest.model.Token
import com.example.quest.network.ApiService
import com.example.quest.utils.AlertHelper
import com.example.quest.utils.Prefs
import com.example.quest.utils.putAny
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signupBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            val nickName = binding.nickNameEt.text.toString()
            val phone = binding.phoneEt.text.toString()
            val code = binding.phoneCodeEt.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                val response = ApiService.doSignup(SignupBody(email, nickName, password, phone))
                if(response.code() == 200 && response.body() != null){
                    startActivity(Intent(this@SignupActivity,LoginActivity::class.java))
                    finish()
                }else{
                    AlertHelper.showAlert(this@SignupActivity,"Signup Failed","Please try again")
                    Log.i("Error",response.body().toString())
                }
            }
        }


    }

}