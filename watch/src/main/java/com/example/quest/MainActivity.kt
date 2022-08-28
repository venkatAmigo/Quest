package com.example.quest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.quest.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInBtn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }

    }
}