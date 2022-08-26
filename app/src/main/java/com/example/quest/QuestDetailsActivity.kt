package com.example.quest

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quest.databinding.ActivityQuestDetailsBinding
import com.example.quest.model.Quest
import com.example.quest.model.QuestsList
import com.google.android.exoplayer2.SimpleExoPlayer

class QuestDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityQuestDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityQuestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.extras
        val quest = intent?.getSerializable("Quest") as Quest
        setUi(quest)


    }
    fun setUi(quest: Quest){
        binding.dateTv.text = quest.startDate
        binding.difficultyTv.text = "Difficulty: ${quest.difficulty}"
        binding.userTv.text = quest.authorName
        binding.categoryTv.text = quest.category?.name
        binding.lbl2Tv.text = quest.tasks?.get(0)?.name ?: "Welcome to Khazan"
        binding.lbl1Tv.text =quest.name
        binding.taskDescTv.text =  quest.description
        binding.questDescTv.text =  quest.description
        binding.taskGoal.text = quest.tasks?.get(0)?.goalType ?: "Scan Qr"
        binding.taskDate.text = quest.tasks?.get(0)?.startDate?: "Scan Qr"
        binding.taskDescTv.text = quest.tasks?.get(0)?.taskCompletionTime.toString()

        val photos = listOf("http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(2).jpg",
        "http://wsk2019.mad.hakta.pro/uploads/files/b8cf53b7499fcc8b%20(1).jpg",
        "http://wsk2019.mad.hakta.pro/uploads/files/Kazan-Expo-Kazan-Ekspo%20(1).jpg")
        val vide = "http://wsk2019.mad.hakta.pro/uploads/files/mad.mp4"
        val audio = "http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3"
        

    }
}