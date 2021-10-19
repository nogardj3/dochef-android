package com.yhjoo.dochef.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.databinding.HomePostlistItemBinding
import com.yhjoo.dochef.utils.BindUtil

class PostListAdapter(private val containerActivity: HomeActivity) :
    ListAdapter<Post, PostListAdapter.HomePostViewHolder>(PostListComparator()) {
    // TODO
    // databinding tag, comment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        return HomePostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.home_postlist_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HomePostViewHolder(val binding: HomePostlistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Post) {
            binding.apply {
                adapter = this@PostListAdapter
                post = item
                activity = containerActivity

                homePostTags.removeAllViews()
                for (tag in item.tags) {
                    val tagcontainer = LayoutInflater.from(homePostTags.context)
                        .inflate(R.layout.view_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView =
                        tagcontainer.findViewById(R.id.tag_post_text)
                    tagview.text = "#$tag"
                    homePostTags.addView(tagcontainer)
                }

                if (item.comments.size != 0) {
                    homePostCommentGroup.isVisible = true

                    BindUtil.loadUserImage(item.comments[0]!!.userImg, homePostCommentUserImg)

                    homePostCommentUserNickname.text = item.comments[0]!!.nickName
                    homePostCommentContents.text = item.comments[0]!!.contents
                    homePostCommentDate.text =
                        BindUtil.millisToText(item.comments[0]!!.dateTime)
                } else homePostCommentGroup.isVisible = false
            }
        }
    }

    class PostListComparator : DiffUtil.ItemCallback<Post>() {
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
