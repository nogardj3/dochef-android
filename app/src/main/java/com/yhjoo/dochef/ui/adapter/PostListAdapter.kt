package com.yhjoo.dochef.ui.adapter

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
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.OtherUtil

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
                mainTimelineTime.text = OtherUtil.convertMillisToText(post.dateTime)

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
                        OtherUtil.convertMillisToText(post.comments[0]!!.dateTime)
                } else mainTimelineCommentGroup.visibility = View.GONE
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
                homePostUserImg.setOnClickListener {
                    userClickListener(post)
                }
                homePostUserNickname.setOnClickListener {
                    userClickListener(post)
                }

                ImageLoaderUtil.loadPostImage(
                    context, post.postImg, homePostPostImg
                )
                ImageLoaderUtil.loadUserImage(
                    context, post.userImg, homePostUserImg
                )
                homePostUserNickname.text = post.nickname
                homePostLikeCount.text = post.likes.size.toString()
                homePostCommentCount.text = post.comments.size.toString()
                homePostContents.text = " " + post.contents
                homePostTime.text = OtherUtil.convertMillisToText(post.dateTime)

                homePostTags.removeAllViews()
                for (tag in post.tags) {
                    val tagcontainer = LayoutInflater.from(context)
                        .inflate(R.layout.view_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tagpost_contents)
                    tagview.text = "#$tag"
                    homePostTags.addView(tagcontainer)
                }

                if (post.comments.size != 0) {
                    homePostCommentGroup.visibility = View.VISIBLE

                    ImageLoaderUtil.loadUserImage(
                        context, post.comments[0]!!.userImg, homePostCommentUserImg
                    )

                    homePostCommentUserNickname.text = post.comments[0]!!.nickName
                    homePostCommentContents.text = post.comments[0]!!.contents
                    homePostCommentDate.text =
                        OtherUtil.convertMillisToText(post.comments[0]!!.dateTime)
                } else homePostCommentGroup.visibility = View.GONE
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
