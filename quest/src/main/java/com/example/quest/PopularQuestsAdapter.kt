package com.example.quest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat.animate
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.model.Quest


class PopularQuestsAdapter(val quests: List<Quest>, val from:String): RecyclerView
.Adapter<PopularQuestsAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.popular_quest_item, parent,
            false
        )
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.itemView.isFocusable = true
        holder.itemView.isFocusableInTouchMode = true
        holder.questNameTv.text = quests[position].name
        holder.questDescTv.text = quests[position].description
        Glide.with(holder.itemView.context).load(quests[position].mainPhoto)
            .into(holder.questIv)
        holder.itemView.isFocusable = true
        holder.itemView.setOnClickListener{
//            val animation = ScaleAnimation(1f,1.2f,1f,1.2f,50f,50f)
//            animation.duration = 3000
//            holder.itemView.startAnimation(animation)
        }
//        holder.itemView.onFocusChangeListener = object: View.OnFocusChangeListener{
//            override fun onFocusChange(view: View?, p1: Boolean) {
//                view?.setBackgroundResource(R.drawable.card_active_bg)
//            }
//
//        }
            holder.questDetailsTv.setOnClickListener {
//                val intent = Intent(holder.itemView.context,QuestDetailsActivity::class.java)
//                intent.putExtra("Quest",quests[position])
//                holder.itemView.context.startActivity(intent)
            }

        }

    override fun getItemCount(): Int {
        return quests.size
    }
    class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questNameTv = itemView.findViewById<TextView>(R.id.quest_name_tv)
        val questDescTv =itemView.findViewById<TextView>(R.id.quest_desc_tv)
        val questDetailsTv =itemView.findViewById<TextView>(R.id.details_tv)
        val questIv =itemView.findViewById<ImageView>(R.id.quest_image_iv)

    }
}