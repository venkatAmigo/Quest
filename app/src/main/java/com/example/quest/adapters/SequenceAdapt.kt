package com.example.quest.adapters

import com.example.quest.model.TaskListItem
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

class SequenceAdapt(val tasks:List<TaskListItem>): SequenceAdapter<TaskListItem>() {
    override fun bindView(sequenceStep: SequenceStep, item: TaskListItem) {
        if(item.status == "COMPLETED" || item.status == "NOT_STARTED"){
            sequenceStep.setActive(true)
        }
        with(sequenceStep){
            setAnchor(item.startDate)
            setTitle(item.name)
            setSubtitle(item.name)
        }
    }

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getItem(position: Int): TaskListItem {
        return tasks[position]
    }
}