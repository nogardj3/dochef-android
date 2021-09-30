package com.yhjoo.dochef.ui.post

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.CommentRepository
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.databinding.PostdetailActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class PostDetailActivity : BaseActivity() {
    private val binding: PostdetailActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.postdetail_activity)
    }
    private val postDetailViewModel: PostDetailViewModel by viewModels {
        PostDetailViewModelFactory(
            PostRepository(applicationContext),
            CommentRepository(applicationContext)
        )
    }
    private lateinit var commentListAdapter: CommentListAdapter

    private lateinit var reviseMenu: MenuItem
    private lateinit var deleteMenu: MenuItem

    private lateinit var userID: String
    private var postID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.postToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userID = DatastoreUtil.getUserBrief(this).userID
        postID = intent.getIntExtra("postID", -1)

        binding.apply {
            lifecycleOwner = this@PostDetailActivity

            commentListAdapter = CommentListAdapter(userID) { view, item ->
                val powerMenu = PowerMenu.Builder(this@PostDetailActivity)
                    .addItem(PowerMenuItem("삭제", false))
                    .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                    .setMenuRadius(10f)
                    .setMenuShadow(5.0f)
                    .setWidth(200)
                    .setTextColor(
                        ContextCompat.getColor(
                            this@PostDetailActivity,
                            R.color.colorPrimary
                        )
                    )
                    .setTextGravity(Gravity.CENTER)
                    .setMenuColor(Color.WHITE)
                    .setBackgroundAlpha(0f)
                    .build()
                powerMenu.onMenuItemClickListener =
                    OnMenuItemClickListener { pos: Int, _: PowerMenuItem? ->
                        if (pos == 0) {
                            MaterialDialog(this@PostDetailActivity).show {
                                message(text = "삭제 하시겠습니까?")
                                positiveButton(text = "확인") {
                                    postDetailViewModel.deleteComment(item.commentID)
                                }
                                negativeButton(text = "취소")
                            }
                            powerMenu.dismiss()
                        }
                    }
                powerMenu.showAsAnchorCenter(view)
            }

            postCommentRecycler.apply {
                layoutManager = LinearLayoutManager(this@PostDetailActivity)
                adapter = commentListAdapter
            }
            postCommentEdittext.addTextChangedListener(object : TextWatcher {
                var prevText = ""

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    prevText = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (binding.postCommentEdittext.lineCount > 3 || s.toString().length >= 120) {
                        binding.postCommentEdittext.setText(prevText)
                        binding.postCommentEdittext.setSelection(prevText.length - 1)
                    }
                }
            })

            postDetailViewModel.userId.value = userID
            postDetailViewModel.postId.value = postID

            postDetailViewModel.postId.observe(this@PostDetailActivity, {
                postDetailViewModel.requestPostDetail()
            })

            postDetailViewModel.postDetail.observe(this@PostDetailActivity, {
                postDetailViewModel.likeThisPost.value = it.likes.contains(postDetailViewModel.userId.value!!)
                setTopView(it)
                postDetailViewModel.requestComments()
            })

            postDetailViewModel.likeThisPost.observe(this@PostDetailActivity, {
                postLike.setImageResource(
                    if (postDetailViewModel.likeThisPost.value!!)
                        R.drawable.ic_favorite_red
                    else
                        R.drawable.ic_favorite_black
                )

                postLike.setOnClickListener {
                    postDetailViewModel.toggleLikePost()
                }
            })

            postDetailViewModel.allComments.observe(this@PostDetailActivity, {
                postCommentEmpty.isVisible = it.isEmpty()
                commentListAdapter.submitList(it) {
                    postCommentRecycler.scrollToPosition(0)
                }
            })

            postDetailViewModel.isDeleted.observe(this@PostDetailActivity, {
                if(it){
                    App.showToast("삭제되었습니다.")
                    finish()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(
            R.menu.menu_post_owner,
            menu
        )

        reviseMenu = menu.findItem(R.id.menu_post_owner_revise)
        deleteMenu = menu.findItem(R.id.menu_post_owner_delete)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_post_owner_revise -> {
                val postInfo = postDetailViewModel.postDetail.value!!
                Intent(this@PostDetailActivity, PostWriteActivity::class.java)
                    .putExtra("MODE", PostWriteActivity.CONSTANTS.UIMODE.REVISE)
                    .putExtra("postID", postInfo.postID)
                    .putExtra("postImg", postInfo.postImg)
                    .putExtra("contents", postInfo.contents)
                    .putExtra("tags", postInfo.tags.toTypedArray()).apply {
                        startActivity(this)
                    }
                true
            }
            R.id.menu_post_owner_delete -> {
                MaterialDialog(this).show {
                    message(text = "삭제하시겠습니까?")
                    positiveButton(text = "확인") {
                        postDetailViewModel.deletePost()
                    }
                    negativeButton(text = "취소")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setTopView(postInfo: Post) {
        binding.apply {
            if (postInfo.userID == userID) {
                reviseMenu.isVisible = true
                deleteMenu.isVisible = true
            }

            ImageLoaderUtil.loadPostImage(
                this@PostDetailActivity,
                postInfo.postImg,
                binding.postPostimg
            )
            ImageLoaderUtil.loadUserImage(
                this@PostDetailActivity,
                postInfo.userImg,
                binding.postUserimg
            )
            postNickname.text = postInfo.nickname
            postContents.text = postInfo.contents
            postTime.text = OtherUtil.millisToText(postInfo.dateTime)
            postLikecount.text = postInfo.likes.size.toString()
            postCommentcount.text = postInfo.comments.size.toString()

            postUserWrapper.setOnClickListener {
                Intent(this@PostDetailActivity, HomeActivity::class.java)
                    .putExtra("userID", postInfo.userID).apply {
                        startActivity(this)
                    }
            }

            postTags.removeAllViews()
            for (tag in postInfo.tags) {
                val tagcontainer =
                    layoutInflater.inflate(R.layout.view_tag_post, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_post_text)
                tagview.text = "#$tag"
                binding.postTags.addView(tagcontainer)
            }
            binding.postCommentOk.setOnClickListener {
                if (binding.postCommentEdittext.text.toString() != "") {
                    postDetailViewModel.createComment(binding.postCommentEdittext.text.toString())
                    hideKeyboard(binding.postCommentEdittext)
                    binding.postCommentEdittext.setText("")
                }
            }
        }
    }
}