package com.example.quest

import android.app.Activity
import android.os.Bundle
import com.example.quest.databinding.ActivityNoTasksBinding

class NoTasksActivity : Activity() {

    private lateinit var binding: ActivityNoTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}