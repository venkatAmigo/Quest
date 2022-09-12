package com.example.quest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.R
import com.example.quest.model.Quest
import com.example.quest.model.Rating

class RatingAdapter(val quests: List<Rating>): RecyclerView.Adapter<RatingAdapter
.RatingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.rating_item,parent,
            false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.userNameTv.text = quests[position].name
        Glide.with(holder.itemView.context).load(quests[position].avatar).error(R.drawable.avatar_placeholder)
            .into(holder.questIv)
        val starOne = holder.starsLt.findViewById<ImageView>(R.id.start_one)
        val starTwo = holder.starsLt.findViewById<ImageView>(R.id.star_two)
        val starThree = holder.starsLt.findViewById<ImageView>(R.id.star_three)
        val starFour = holder.starsLt.findViewById<ImageView>(R.id.star_four)
        val starFive = holder.starsLt.findViewById<ImageView>(R.id.star_five)
        val a = listOf(
            starOne,
            starTwo,
            starThree,
            starFour,
            starFive
        )
        for (i in 0 until quests[position].rating) {
            a[i].setImageResource(R.drawable.star)
        }
        for (i in (quests[position].rating)..4) {
            a[i].setImageResource(R.drawable.start_stroke)
        }
    }

    override fun getItemCount(): Int {
        return quests.size
    }
    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTv: TextView = itemView.findViewById<TextView>(R.id.name_tv)
        val questIv: ImageView =itemView.findViewById<ImageView>(R.id.profile_iv)
        val starsLt: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.rating_stars)
    }
}