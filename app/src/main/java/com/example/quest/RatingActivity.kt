package com.example.quest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quest.adapters.CommentsAdapter
import com.example.quest.adapters.RatingAdapter
import com.example.quest.databinding.ActivityRatingBinding
import com.example.quest.model.Rating

class RatingActivity : AppCompatActivity() {
    lateinit var binding:ActivityRatingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image = "https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes.png"
        val ratings = listOf(Rating("Venkat",5,image)
        ,Rating("John",4,""),Rating("Carphon",4,image),
            Rating("Charlie",3,image),Rating("Willson",3,"")
        ,Rating("Holygrace",2,image))
        val ratingsAuthors = ratings.sortedBy { it.name }

        binding.topAuthorsRecycle.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,
            false)
        binding.topPlayersRecycle.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,
            false)
        binding.topPlayersRecycle.adapter = RatingAdapter(ratings)
        binding.topAuthorsRecycle.adapter = RatingAdapter(ratingsAuthors)


    }
}