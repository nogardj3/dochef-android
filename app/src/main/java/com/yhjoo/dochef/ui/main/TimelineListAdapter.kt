package com.yhjoo.dochef.ui.main

import android.view.LayoutInflater
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

class TimelineListAdapter(
    private val containerFragment: TimelineFragment
) :
    ListAdapter<Post, TimelineListAdapter.TimelineViewHolder>(TimelineListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
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
        fun bind(item: Post) {
            binding.apply {
                fragment = containerFragment
                post = item

                mainTimelineTags.removeAllViews()
                for (tag in item.tags) {
                    val tagcontainer = LayoutInflater.from(mainTimelineTags.context)
                        .inflate(R.layout.view_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_post_text)
                    tagview.text = "#$tag"
                    mainTimelineTags.addView(tagcontainer)
                }
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
