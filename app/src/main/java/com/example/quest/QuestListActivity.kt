package com.example.quest

import android.app.Dialog
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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

//This is part of session - 2
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
        binding.searchResultBtn.setOnClickListener(this)
        binding.spinner.adapter = ArrayAdapter<String>(this, android.R.layout
            .simple_spinner_dropdown_item, listOf("Themed","For children","In car"))
        binding.addChip.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_tag)
            val addBtn = dialog.findViewById<Button>(R.id.add_dlg_btn)
            val closeBtn = dialog.findViewById<Button>(R.id.close_dialog_btn)
            val tag = dialog.findViewById<EditText>(R.id.add_tag_et)
            dialog.show()
            addBtn.setOnClickListener {
                val chip = Chip(this)
                chip.text = tag.text.toString()
                chip.setOnCloseIconClickListener {
                    binding.chipGroup.removeView(chip)
                }
                chip.setChipBackgroundColorResource(R.color.text_color);
                chip.isCloseIconVisible = true;
                chip.setTextColor(getResources().getColor(R.color.white))
                binding.chipGroup.addView(chip)
                dialog.dismiss()
            }
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

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
                binding.difficultBtn.setBackgroundResource(R.drawable.background_green)
                binding.popularBtn.setBackgroundResource(R.drawable.selection_button)
                binding.newBtn.setBackgroundResource(R.drawable.selection_button)
                adapter = CategoryAdapter(quest.content.sortedBy {
                    it.difficulty
                })
                binding.questListRecycler.adapter = adapter
            }
            R.id.new_btn -> {
                binding.newBtn.setBackgroundResource(R.drawable.background_green)
                binding.difficultBtn.setBackgroundResource(R.drawable.selection_button)
                binding.popularBtn.setBackgroundResource(R.drawable.selection_button)
                adapter = CategoryAdapter(quest.content.sortedBy {
                    it.creationDate
                })
                binding.questListRecycler.adapter = adapter
            }
            R.id.popular_btn -> {
                binding.popularBtn.setBackgroundResource(R.drawable.background_green)
                binding.difficultBtn.setBackgroundResource(R.drawable.selection_button)
                binding.newBtn.setBackgroundResource(R.drawable.selection_button)
                adapter = CategoryAdapter(quest.content.sortedBy {
                    it.difficulty
                })
                binding.questListRecycler.adapter = adapter
            }
            R.id.search_result_btn->{
                val word = binding.wordsTv.text.toString()
                adapter = CategoryAdapter(quest.content.filter{
                    it.description?.contains(word) == true || it.name.contains(word)
                })
                binding.questListRecycler.adapter = adapter
            }
        }
    }
}