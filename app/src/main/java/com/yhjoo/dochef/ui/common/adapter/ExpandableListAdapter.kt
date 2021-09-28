package com.yhjoo.dochef.ui.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.ExpandableItem
import com.yhjoo.dochef.databinding.ExpandableItemBinding
import com.yhjoo.dochef.utils.OtherUtil

class ExpandableListAdapter(
    private val showDate: Boolean,
) :
    ListAdapter<ExpandableItem, ExpandableListAdapter.ExpandableViewHolder>(ExpandableListComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableViewHolder {
        context = parent.context

        return ExpandableViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.expandable_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExpandableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpandableViewHolder(val binding: ExpandableItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expandableItem: ExpandableItem) {
            binding.apply {
                root.setOnClickListener {
                    expandableItem.expanded = !expandableItem.expanded
                    notifyDataSetChanged()
                }

                expTitleTitle.text = expandableItem.title

                expandableContents.isVisible = expandableItem.expanded
                expContentsContents.text = expandableItem.contents
                expContentsDate.isVisible = showDate
                expContentsDate.text = OtherUtil.millisToText(expandableItem.dateTime)
            }
        }
    }

    class ExpandableListComparator : DiffUtil.ItemCallback<ExpandableItem>() {
        override fun areItemsTheSame(
            oldItem: ExpandableItem,
            newItem: ExpandableItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ExpandableItem,
            newItem: ExpandableItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
