package com.example.quest.adapters

import android.widget.Toast
import com.example.quest.R
import com.example.quest.model.TaskListItem
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

class SequenceAdapt(val tasks: List<TaskListItem>,val onclick: (Int) -> Unit):
    SequenceAdapter<TaskListItem>() {
    override fun bindView(sequenceStep: SequenceStep, item: TaskListItem) {
        if(item.status == "COMPLETED" || item.status == "NOT_STARTED"){
            sequenceStep.setActive(true)
        }
        with(sequenceStep){
            setTitle(item.name)
            setSubtitle(item.name)
            setSubtitleTextAppearance(R.style.sequence_text_sub)
            setTitleTextAppearance(R.style.sequence_text_title)
            setOnClickListener {
                Toast.makeText(sequenceStep.context, "sequence ${item.name}", Toast.LENGTH_SHORT).show()
                onclick(tasks.indexOf(item))
            }
        }
    }

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getItem(position: Int): TaskListItem {
        return tasks[position]
    }
}