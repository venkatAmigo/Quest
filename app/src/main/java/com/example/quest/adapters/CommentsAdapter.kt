package com.example.quest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quest.R
import com.example.quest.model.comments.Content
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter(val comments:List<Content>): RecyclerView.Adapter<CommentsAdapter
.CommentsViewHolder>
    () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item,parent,false)
        return  CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
       holder.nameTv.text = comments[position].author.name
        holder.commentTv.text = comments[position].text
        holder.dateTv.text = comments[position].date
        Glide.with(holder.itemView.context).load(comments[position].author.avatar).into(holder.profile)
        val starOne = holder.starsLt.findViewById<ImageView>(R.id.start_one)
        val starTwo = holder.starsLt.findViewById<ImageView>(R.id.star_two)
        val starThree = holder.starsLt.findViewById<ImageView>(R.id.star_three)
        val starFour = holder.starsLt.findViewById<ImageView>(R.id.star_four)
        val starFive = holder.starsLt.findViewById<ImageView>(R.id.star_five)
        val a = listOf(
            starOne,
            starTwo,
            starThree,
            starFour,
            starFive
        )
        for (i in 0..(comments[position].rating-1)) {
            a[i].setImageResource(R.drawable.star)
        }
        for (i in (comments[position].rating)..4) {
            a[i].setImageResource(R.drawable.start_stroke)
        }
    }
    override fun getItemCount(): Int {
        return comments.size
    }
    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<CircleImageView>(R.id.cmt_profile_iv)
        val nameTv = itemView.findViewById<TextView>(R.id.cmt_name_tv)
        val commentTv = itemView.findViewById<TextView>(R.id.cmt_comment_tv)
        val dateTv = itemView.findViewById<TextView>(R.id.cmt_date_tv)
        val starsLt = itemView.findViewById<ConstraintLayout>(R.id.rating_stars)
    }

}