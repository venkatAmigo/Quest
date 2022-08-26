package com.example.quest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quest.adapters.CategoryAdapter
import com.example.quest.database.QuestDatabase
import com.example.quest.databinding.ActivityQuestListBinding
import com.example.quest.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestListActivity : AppCompatActivity() {
    lateinit var quest: QuestsList
    lateinit var binding: ActivityQuestListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuestListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.extras
        quest = intent?.getSerializable("Quest") as QuestsList
        binding.questListRecycler.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,
            false)
        binding.questListRecycler.adapter = CategoryAdapter(quest.content)

    }
}