package com.example.quest.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.R
import com.example.quest.model.Achievements

class AchievementAdapter (val achievements: List<Achievements>): RecyclerView.Adapter<AchievementAdapter
.AchivementViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchivementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.achievement_item,parent,
            false)
        return AchivementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchivementViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(achievements[position].image)
            .into(holder.achieveIv)
        holder.achieveIv.setOnClickListener {
            val dialog = Dialog(holder.itemView.context)
            dialog.setContentView(R.layout.acievement_dialog)
            val achieveIv = dialog.findViewById<ImageView>(R.id.achive_iv)
            val achieveText = dialog.findViewById<TextView>(R.id.achieve_text)
            val achieveNameText = dialog.findViewById<TextView>(R.id.achievement_name)
            val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
            dialog.show()
            achieveNameText.text = achievements[position].name
            achieveIv.setImageResource(achievements[position].image)
            achieveText.text = achievements[position].desc
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return achievements.size
    }

    class AchivementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achieveIv =itemView.findViewById<ImageView>(R.id.achieve_iv)

    }
}