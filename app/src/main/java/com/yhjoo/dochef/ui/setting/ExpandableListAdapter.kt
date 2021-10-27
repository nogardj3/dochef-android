package com.yhjoo.dochef.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.ExpandableItem
import com.yhjoo.dochef.databinding.ExpandableItemBinding

class ExpandableListAdapter(
    private val showDate: Boolean
) :
    ListAdapter<ExpandableItem, ExpandableListAdapter.ExpandableViewHolder>(ExpandableListComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableViewHolder {
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
        fun bind(item: ExpandableItem) {
            binding.apply {
                dateShow = showDate
                expandableItem = item

                root.setOnClickListener {
                    item.expanded = !item.expanded
                    notifyItemChanged(adapterPosition)
                }
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
