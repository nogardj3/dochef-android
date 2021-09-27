package com.yhjoo.dochef.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.databinding.MainTimelineItemBinding
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.OtherUtil

class TimelineListAdapter(
    private val userClickListener: (Post) -> Unit,
    private val itemClickListener: (Post) -> Unit
) :
    ListAdapter<Post, TimelineListAdapter.TimelineViewHolder>(TimelineListComparator()) {
    lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        context = parent.context

        return TimelineViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.main_timeline_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TimelineViewHolder(val binding: MainTimelineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener(post)
                }
                mainTimelineUserImg.setOnClickListener {
                    userClickListener(post)
                }
                mainTimelineUserNickname.setOnClickListener {
                    userClickListener(post)
                }

                ImageLoaderUtil.loadPostImage(
                    context, post.postImg, mainTimelinePostImg
                )
                ImageLoaderUtil.loadUserImage(
                    context, post.userImg, mainTimelineUserImg
                )
                mainTimelineUserNickname.text = post.nickname
                mainTimelineLikeCount.text = post.likes.size.toString()
                mainTimelineCommentCount.text = post.comments.size.toString()
                mainTimelineContents.text = " " + post.contents
                mainTimelineTime.text = OtherUtil.millisToText(post.dateTime)

                mainTimelineTags.removeAllViews()
                for (tag in post.tags) {
                    val tagcontainer = LayoutInflater.from(context)
                        .inflate(R.layout.view_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tagpost_contents)
                    tagview.text = "#$tag"
                    mainTimelineTags.addView(tagcontainer)
                }

                if (post.comments.size != 0) {
                    mainTimelineCommentGroup.visibility = View.VISIBLE

                    ImageLoaderUtil.loadUserImage(
                        context, post.comments[0]!!.userImg, mainTimelineCommentUserImg
                    )

                    mainTimelineCommentUserNickname.text = post.comments[0]!!.nickName
                    mainTimelineCommentContents.text = post.comments[0]!!.contents
                    mainTimelineCommentDate.text =
                        OtherUtil.millisToText(post.comments[0]!!.dateTime)
                } else mainTimelineCommentGroup.visibility = View.GONE
            }
        }
    }

    class TimelineListComparator : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(
            oldItem: Post,
            newItem: Post
        ): Boolean {
            return oldItem.postID == newItem.postID
        }

        override fun areContentsTheSame(
            oldItem: Post,
            newItem: Post
        ): Boolean {
            return oldItem == newItem
        }
    }
}
