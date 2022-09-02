package com.example.quest

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quest.adapters.CategoryAdapter
import com.example.quest.database.QuestDatabase
import com.example.quest.databinding.ActivityQuestListBinding
import com.example.quest.model.*
import com.example.quest.utils.AlertHelper
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestListActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var quest: QuestsList
    lateinit var binding: ActivityQuestListBinding
    lateinit var adapter: CategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent.extras
        quest = intent?.getSerializable("Quest") as QuestsList
        binding.questListRecycler.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL,
            false
        )
        adapter = CategoryAdapter(quest.content)
        binding.questListRecycler.adapter = adapter
        binding.allBtn.setOnClickListener(this)
        binding.favBtn.setOnClickListener(this)
        binding.difficultBtn.setOnClickListener(this)
        binding.popularBtn.setOnClickListener(this)
        binding.newBtn.setOnClickListener(this)
        binding.addChip.setOnClickListener{
            val chip = Chip(this)
            chip.setText("Chip 1")
            chip.setOnCloseIconClickListener {
                binding.chipGroup.removeView(chip)
            }
            chip.setChipBackgroundColorResource(R.color.text_color);
            chip.isCloseIconVisible = true;
            chip.setTextColor(getResources().getColor(R.color.white))
            binding.chipGroup.addView(chip)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.all_btn -> {
                Toast.makeText(this, "all_btn", Toast.LENGTH_SHORT).show()
                binding.questListRecycler.adapter = CategoryAdapter(quest.content)
                binding.allBtn.setBackgroundResource(R.drawable.background_green)
                binding.favBtn.setBackgroundResource(R.drawable.selection_button)
            }
            R.id.fav_btn -> {
                binding.favBtn.setBackgroundResource(R.drawable.background_green)
                binding.allBtn.setBackgroundResource(R.drawable.selection_button)
                CoroutineScope(Dispatchers.IO).launch {
                    val quests = QuestDatabase.getDatabaseInstance(this@QuestListActivity).getDao()
                    .getAll()
                    withContext(Dispatchers.Main) {
                        quests.observe(this@QuestListActivity) { list ->
                            val filtered = quest.content.filter {
                                var contains = false
                                list.forEach { listItem ->
                                    Log.i("FAV_SORT",listItem.toString())
                                    if(it.id == listItem.id)
                                        contains = true
                                }
                                contains
                            }
                            binding.questListRecycler.adapter = CategoryAdapter(filtered)
                        }
                    }
                }
            }
            R.id.difficult_btn -> {
                adapter = CategoryAdapter(quest.content.sortedBy {
                    it.difficulty
                })
                binding.questListRecycler.adapter = adapter
            }
            R.id.new_btn -> {
                adapter = CategoryAdapter(quest.content.sortedBy {
                    it.creationDate
                })
                binding.questListRecycler.adapter = adapter
            }
            R.id.popular_btn -> {
                adapter = CategoryAdapter(quest.content.sortedBy {
                    it.difficulty
                })
                binding.questListRecycler.adapter = adapter
            }
        }
    }
}