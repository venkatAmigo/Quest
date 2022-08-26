package com.example.quest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.R
import com.example.quest.model.Quest

class RatingAdapter(val quests: List<Quest>): RecyclerView.Adapter<RatingAdapter
.RatingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.rating_item,parent,
            false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.userNameTv.text = quests[position].name
        Glide.with(holder.itemView.context).load(quests[position].mainPhoto)
            .into(holder.questIv)

    }

    override fun getItemCount(): Int {
        return quests.size
    }
    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTv = itemView.findViewById<TextView>(R.id.name_tv)
        val questIv =itemView.findViewById<ImageView>(R.id.profile_iv)
        val starOneIv =itemView.findViewById<ImageView>(R.id.start_one)
        val starTwoIv =itemView.findViewById<ImageView>(R.id.star_two)
        val starThreeeIv =itemView.findViewById<ImageView>(R.id.star_three)
        val starFourIv =itemView.findViewById<ImageView>(R.id.star_four)
        val starFiveIv =itemView.findViewById<ImageView>(R.id.star_five)

    }
}