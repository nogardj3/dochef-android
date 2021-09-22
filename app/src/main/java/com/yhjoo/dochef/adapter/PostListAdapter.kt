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
import com.yhjoo.dochef.databinding.PostlistItemBinding
import com.yhjoo.dochef.model.Post
import com.yhjoo.dochef.utilities.GlideImageLoadDelegator
import com.yhjoo.dochef.utilities.Utils

class PostListAdapter(
    private val userClickListener: (Post) -> Unit,
    private val itemClickListener: (Post) -> Unit
) :
    ListAdapter<Post, PostListAdapter.PostListViewHolder>(
        PostListComparator()
    ) {
    companion object{
        object LAYOUTTYPE{
            const val MAIN_TIMELINE = 0
            const val HOME = 1
        }
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        context = parent.context

        val binding = DataBindingUtil.inflate<PostlistItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.postlist_item,
            parent,
            false
        )

        return PostListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostListViewHolder(val binding: PostlistItemBinding) :
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
