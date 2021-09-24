package com.yhjoo.dochef.adapter

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
import com.yhjoo.dochef.databinding.HomePostlistItemBinding
import com.yhjoo.dochef.databinding.MainTimelineItemBinding
import com.yhjoo.dochef.model.Post
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class PostListAdapter(
    private val viewholderType: Int,
    private val userClickListener: (Post) -> Unit,
    private val itemClickListener: (Post) -> Unit
) :
    ListAdapter<Post, RecyclerView.ViewHolder>(PostListComparator()) {
    companion object {
        const val MAIN_TIMELINE = 0
        const val HOME = 1
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        return when (viewholderType) {
            MAIN_TIMELINE ->
                MainTimelineViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.main_timeline_item,
                        parent,
                        false
                    )
                )
            else -> HomePostViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.home_postlist_item,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainTimelineViewHolder -> holder.bind(getItem(position))
            is HomePostViewHolder -> holder.bind(getItem(position))
        }
    }

    inner class MainTimelineViewHolder(val binding: MainTimelineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener(post)
                }
                timelineUserImg.setOnClickListener {
                    userClickListener(post)
                }
                timelineUserNickname.setOnClickListener {
                    userClickListener(post)
                }

                GlideImageLoadDelegator.loadPostImage(
                    context, post.postImg, timelinePostImg
                )
                GlideImageLoadDelegator.loadUserImage(
                    context, post.userImg, timelineUserImg
                )
                timelineUserNickname.text = post.nickname
                timelineLikeCount.text = post.likes.size.toString()
                timelineCommentCount.text = post.comments.size.toString()
                timelineContents.text = " " + post.contents
                timelineTime.text = Utils.convertMillisToText(post.dateTime)

                timelineTags.removeAllViews()
                for (tag in post.tags) {
                    val tagcontainer = LayoutInflater.from(context)
                        .inflate(R.layout.v_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_post_text)
                    tagview.text = "#$tag"
                    timelineTags.addView(tagcontainer)
                }

                if (post.comments.size != 0) {
                    timelineCommentGroup.visibility = View.VISIBLE

                    GlideImageLoadDelegator.loadUserImage(
                        context, post.comments[0]!!.userImg, timelineCommentUserImg
                    )

                    timelineCommentUserNickname.text = post.comments[0]!!.nickName
                    timelineCommentContents.text = post.comments[0]!!.contents
                    timelineCommentDate.text =
                        Utils.convertMillisToText(post.comments[0]!!.dateTime)
                } else timelineCommentGroup.visibility = View.GONE
            }
        }
    }

    inner class HomePostViewHolder(val binding: HomePostlistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.apply {
                root.setOnClickListener {
                    itemClickListener(post)
                }
                postlistUserImg.setOnClickListener {
                    userClickListener(post)
                }
                postlistUserNickname.setOnClickListener {
                    userClickListener(post)
                }

                GlideImageLoadDelegator.loadPostImage(
                    context, post.postImg, postlistPostImg
                )
                GlideImageLoadDelegator.loadUserImage(
                    context, post.userImg, postlistUserImg
                )
                postlistUserNickname.text = post.nickname
                postlistLikeCount.text = post.likes.size.toString()
                postlistCommentCount.text = post.comments.size.toString()
                postlistContents.text = " " + post.contents
                postlistTime.text = Utils.convertMillisToText(post.dateTime)

                postlistTags.removeAllViews()
                for (tag in post.tags) {
                    val tagcontainer = LayoutInflater.from(context)
                        .inflate(R.layout.v_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_post_text)
                    tagview.text = "#$tag"
                    postlistTags.addView(tagcontainer)
                }

                if (post.comments.size != 0) {
                    postlistCommentGroup.visibility = View.VISIBLE

                    GlideImageLoadDelegator.loadUserImage(
                        context, post.comments[0]!!.userImg, postlistCommentUserImg
                    )

                    postlistCommentUserNickname.text = post.comments[0]!!.nickName
                    postlistCommentContents.text = post.comments[0]!!.contents
                    postlistCommentDate.text =
                        Utils.convertMillisToText(post.comments[0]!!.dateTime)
                } else postlistCommentGroup.visibility = View.GONE
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
