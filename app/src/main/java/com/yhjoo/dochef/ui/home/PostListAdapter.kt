package com.yhjoo.dochef.ui.home

import android.content.Context
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
import com.yhjoo.dochef.utils.ImageLoaderUtil
import com.yhjoo.dochef.utils.OtherUtil

class PostListAdapter(
    private val userClickListener: (Post) -> Unit,
    private val itemClickListener: (Post) -> Unit
) :
    ListAdapter<Post, PostListAdapter.HomePostViewHolder>(PostListComparator()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        context = parent.context

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
                homePostTime.text = OtherUtil.millisToText(post.dateTime)

                homePostTags.removeAllViews()
                for (tag in post.tags) {
                    val tagcontainer = LayoutInflater.from(context)
                        .inflate(R.layout.view_tag_post, null) as LinearLayout
                    val tagview: AppCompatTextView =
                        tagcontainer.findViewById(R.id.tag_post_text)
                    tagview.text = "#$tag"
                    homePostTags.addView(tagcontainer)
                }


                if (post.comments.size != 0) {
                    homePostCommentGroup.isVisible = true

                    ImageLoaderUtil.loadUserImage(
                        context, post.comments[0]!!.userImg, homePostCommentUserImg
                    )

                    homePostCommentUserNickname.text = post.comments[0]!!.nickName
                    homePostCommentContents.text = post.comments[0]!!.contents
                    homePostCommentDate.text =
                        OtherUtil.millisToText(post.comments[0]!!.dateTime)
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
