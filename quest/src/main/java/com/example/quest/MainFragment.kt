package com.example.quest

import java.util.Collections
import java.util.Timer
import java.util.TimerTask

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.quest.model.Quest
import com.example.quest.model.QuestsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : VerticalGridSupportFragment(), OnItemViewClickedListener,
    OnItemViewSelectedListener {

    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics

    lateinit var currentQuest:List<Quest>
    lateinit var arrayObjectAdapter:ArrayObjectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val verticalGridPresenter =  VerticalGridPresenter()
        verticalGridPresenter.numberOfColumns = 3
        verticalGridPresenter.enableChildRoundedCorners(true)
        verticalGridPresenter.shadowEnabled = true
        gridPresenter = verticalGridPresenter


        arrayObjectAdapter = ArrayObjectAdapter(MyPresenter())
        adapter = arrayObjectAdapter
        getQuests()


        setupUIElements()
        prepareBackgroundManager()
        onItemViewClickedListener = this
        setOnItemViewSelectedListener(this)

    }
    fun setFilter(category:String){

        val newList =currentQuest.filter {
            it.category.name == category
        }
        arrayObjectAdapter.clear()
        arrayObjectAdapter.addAll(0,newList )
        arrayObjectAdapter.notifyArrayItemRangeChanged(0,newList.size)
    }
    fun getQuests(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response =ApiService.getPopularQuests("ff4ae715-9936-488c-c7fd-1739e2551415")
                if (response?.code() == 200 && response.body() != null) {

                    val result = response.body() as QuestsList
                    currentQuest = result.content
                    arrayObjectAdapter.addAll(0,currentQuest )
                } else {
                    Toast.makeText(
                        requireContext(), "${
                            response?.errorBody()?.charStream()
                                ?.readText()
                        }", Toast
                            .LENGTH_SHORT
                    ).show()
                }
            }catch (exc :Exception){
                Log.i("FRE","exception ${exc.localizedMessage}")
                Toast.makeText(requireContext(), "exception ${exc.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
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

    class MyPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            return  ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.popular_quest_item,
                parent, false))
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val quest = item as Quest
            val textView = viewHolder?.view?.findViewById<TextView>(R.id.quest_name_tv)
            textView?.text = quest.name
            val questDesc = viewHolder?.view?.findViewById<TextView>(R.id.quest_desc_tv)
            questDesc?.text = quest.description
            val questImage = viewHolder?.view?.findViewById<ImageView>(R.id.quest_image_iv)
            viewHolder?.view?.let { it.context?.let { it1 ->
                if (questImage != null) {
                    Glide.with(it1).load(
                        quest.mainPhoto).into(questImage)
                }
            } }
            viewHolder?.view?.isFocusable = true
            viewHolder?.view?.isFocusableInTouchMode = true
            viewHolder?.view?.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color
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
        val intent = Intent(requireContext(),TasksListActivity::class.java)
        val questItem = item as Quest
        intent.putExtra("TASKS",questItem)
        startActivity(intent)
        Toast.makeText(itemViewHolder?.view?.context, "Clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {

    }


}