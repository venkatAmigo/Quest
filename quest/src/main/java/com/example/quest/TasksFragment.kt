package com.example.quest

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.example.quest.model.Quest
import com.example.quest.model.Task
import com.example.quest.model.TaskParticipant
import com.example.quest.model.TaskParticipantItem
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.random.Random

class TasksFragment(val tasks: Quest) : VerticalGridSupportFragment(), OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics

    lateinit var currentQuest:TaskParticipant
    lateinit var arrayObjectAdapter: ArrayObjectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val verticalGridPresenter =  VerticalGridPresenter()
        verticalGridPresenter.numberOfColumns = 3
        verticalGridPresenter.enableChildRoundedCorners(true)
        verticalGridPresenter.shadowEnabled = true
        gridPresenter = verticalGridPresenter

        arrayObjectAdapter = ArrayObjectAdapter(MyPresenter())
        adapter = arrayObjectAdapter
        // Log.i("TLSKS",tasks)
        //val json = JSONArray(tasks)
        //val tskList = Gson().fromJson(tasks,Quest::class.java)
        arrayObjectAdapter.addAll(0, tasks.tasks)

        setupUIElements()
        prepareBackgroundManager()
        onItemViewClickedListener = this
        setOnItemViewSelectedListener(this)

    }

    private fun setupUIElements() {
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.search_opaque)

    }
    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(requireActivity(), R.drawable.movie)
        mBackgroundManager.drawable = mDefaultBackground
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    class MyPresenter() : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            return  ViewHolder(
                LayoutInflater.from(parent?.context).inflate(R.layout.popular_quest_item,
                parent, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val quest = item as Task
            val textView = viewHolder?.view?.findViewById<TextView>(R.id.quest_name_tv)
            textView?.text = quest.name
            val questDesc = viewHolder?.view?.findViewById<TextView>(R.id.quest_desc_tv)
            questDesc?.text = quest.name

            val taskCountTv = viewHolder?.view?.findViewById<TextView>(R.id.details_tv)
            taskCountTv?.setCompoundDrawables(ResourcesCompat.getDrawable(viewHolder.view.resources,R
                .drawable.user,null),null,null,null)
            val count = (1..10).random()
            taskCountTv?.text = "$count Players Online"
            viewHolder?.view?.isFocusable = true
            viewHolder?.view?.isFocusableInTouchMode = true
            viewHolder?.view?.setBackgroundColor(
                ContextCompat.getColor(viewHolder.view.context, R.color
                .default_background))

        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {

        }

    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        startActivity(Intent(requireContext(),PartcipantsActivity::class.java))
    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }


}