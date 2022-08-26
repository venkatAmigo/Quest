package com.example.quest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.R
import com.example.quest.model.Quest

class CategoryAdapter(val quests: List<Quest>): RecyclerView.Adapter<CategoryAdapter
.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.quest_section_item,parent,
            false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.questNameTv.text = quests[position].category?.name
        holder.questDescTv.text = quests[position].category?.description
        holder.itemRecyler.layoutManager = GridLayoutManager(holder.itemView.context,2,RecyclerView
            .VERTICAL,false)
        val questCat = quests.filter {
            quest ->  quest.category?.name == quests[position].category?.name
        }
        holder.itemRecyler.adapter = PopularQuestsAdapter(questCat,"sections")

    }

    override fun getItemCount(): Int {
        return quests.size
    }
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questNameTv = itemView.findViewById<TextView>(R.id.category_tv)
        val questDescTv =itemView.findViewById<TextView>(R.id.desc_tv)
        val itemRecyler =itemView.findViewById<RecyclerView>(R.id.section_inside_recycler)
    }
}