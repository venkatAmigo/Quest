package com.example.quest

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.quest.model.Quest

class TasksListActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)
        val tasks = intent.getSerializableExtra("TASKS") as Quest
        if (savedInstanceState == null) {
            tasks?.let { TasksFragment(it) }?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, it)
                    .commitNow()
            }
        }
    }
}