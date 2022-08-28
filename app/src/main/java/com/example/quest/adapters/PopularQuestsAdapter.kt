package com.example.quest.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.QuestDetailsActivity
import com.example.quest.QuestListActivity
import com.example.quest.R
import com.example.quest.database.QuestDatabase
import com.example.quest.model.DummyTask
import com.example.quest.model.Photo
import com.example.quest.model.Quest
import com.example.quest.model.Quests
import com.example.quest.utils.AlertHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PopularQuestsAdapter(val quests: List<Quest>, val from:String): RecyclerView
.Adapter<PopularQuestsAdapter
.PopularViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.popular_quest_item, parent,
            false
        )
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.questNameTv.text = quests[position].name
        holder.questDescTv.text = quests[position].description
        Glide.with(holder.itemView.context).load(quests[position].mainPhoto)
            .into(holder.questIv)
        if (from == "sections") {
            holder.faveIv.visibility = View.VISIBLE
            var fav = isItFav(quests[position].id,holder.itemView.context)
            if (fav) {
                holder.faveIv.setImageResource(R.drawable.fav_stroke)
                fav = !fav
            } else {
                holder.faveIv.setImageResource(R.drawable.not_fav)
                fav = !fav
            }
            holder.faveIv.setOnClickListener {
                if (fav) {
                    holder.faveIv.setImageResource(R.drawable.fav_stroke)
                    deleteFromFav(quests[position].id,holder.itemView.context)
                    fav = !fav
                } else {
                    holder.faveIv.setImageResource(R.drawable.not_fav)
                    addToFav(quests[position],holder.itemView.context)
                    fav = !fav
                }
            }
            holder.questDetailsTv.setOnClickListener {
                val intent = Intent(holder.itemView.context,QuestDetailsActivity::class.java)
                intent.putExtra("Quest",quests[position])
                holder.itemView.context.startActivity(intent)
            }

        }
    }
    fun isItFav(id: Int, context: Context):Boolean{
        var favQuestNo = 0
        CoroutineScope(Dispatchers.IO).launch {
            val favQuest = QuestDatabase.getDatabaseInstance(context).getDao().getOne(id)
            if(favQuest !=null && favQuest.size == 1){
                if(favQuest[0].id == id) {
                    favQuestNo = 1
                    Log.i("ISFAV","TRUEE")
                }
            }

        }
        return favQuestNo == 1
    }
    fun deleteFromFav(id: Int, context: Context){
        var number =0
        CoroutineScope(Dispatchers.IO).launch {
           number = QuestDatabase.getDatabaseInstance(context).getDao().delete(id)
        }
        if(number>0){
            AlertHelper.showAlert(context,"Deletion","Quest delete from favourites")
        }else{
            AlertHelper.showAlert(context,"Error","Deletion error")
        }
    }

    fun addToFav(quest:Quest, context: Context) {
        CoroutineScope(Dispatchers.IO).launch{
            val newQuest = Quests(
                quest.id,
                quest.name,
                quest.description,
                quest.photos?.let { Photo(it) },
                quest.creationDate,
                quest.startDate,
                quest.endDate,
                quest.mainPhoto,
                quest.difficulty,
                quest.category,
                quest.tags?.let { Photo(it) },
                quest.tasks?.let { DummyTask(it) })
            QuestDatabase.getDatabaseInstance(context).getDao().insert(newQuest)
//            withContext(Dispatchers.Main) {
//                if(i>0){
//                    AlertHelper.showAlert(context,"Added","Quest added to favourites")
//                }else{
//                    AlertHelper.showAlert(context,"Error","Adding to favourites error")
//                }
//                dbQuest.observe() { list ->
//                    list.forEach {
//                        Log.i("LSTQUEST", it.name)
//                    }
//
//                }
//            }
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
        val faveIv =itemView.findViewById<ImageView>(R.id.fav_icon)

    }
}