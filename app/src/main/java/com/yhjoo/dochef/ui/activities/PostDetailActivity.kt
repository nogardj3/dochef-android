package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.databinding.APostdetailBinding
import com.yhjoo.dochef.ui.adapter.CommentListAdapter
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RetrofitServices.CommentService
import com.yhjoo.dochef.utils.RetrofitServices.PostService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PostDetailActivity : BaseActivity() {
    private val binding: APostdetailBinding by lazy { APostdetailBinding.inflate(layoutInflater) }

    private lateinit var postService: PostService
    private lateinit var commentService: CommentService
    private lateinit var commentListAdapter: CommentListAdapter
    private lateinit var commentList: ArrayList<Comment>

    private lateinit var reviseMenu: MenuItem
    private lateinit var deleteMenu: MenuItem

    private lateinit var userID: String
    private lateinit var postInfo:Post
    private var postID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.postToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        postService = RetrofitBuilder.create(this, PostService::class.java)
        commentService = RetrofitBuilder.create(this, CommentService::class.java)

        userID = Utils.getUserBrief(this).userID
        postID = intent.getIntExtra("postID", -1)

        commentListAdapter = CommentListAdapter(userID)
        commentListAdapter.setOnItemChildClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
            val powerMenu = PowerMenu.Builder(this)
                .addItem(PowerMenuItem("삭제", false))
                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                .setMenuRadius(10f)
                .setMenuShadow(5.0f)
                .setWidth(200)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTextGravity(Gravity.CENTER)
                .setMenuColor(Color.WHITE)
                .setBackgroundAlpha(0f)
                .build()
            powerMenu.onMenuItemClickListener =
                OnMenuItemClickListener { pos: Int, _: PowerMenuItem? ->
                    if (pos == 0) {
                        MaterialDialog(this).show {
                            message(text = "삭제 하시겠습니까?")
                            positiveButton(text = "확인") {
                                removeComment((baseQuickAdapter.getItem(position) as Comment).commentID)
                            }
                            negativeButton(text = "취소")
                        }
                        powerMenu.dismiss()
                    }
                }
            powerMenu.showAsAnchorCenter(view)
        }

        binding.postCommentRecycler.layoutManager = LinearLayoutManager(this)
        binding.postCommentRecycler.adapter = commentListAdapter

        if (App.isServerAlive)
            loadData()
        else {
            postInfo = (DataGenerator.make<Any>(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_POST)
            ) as ArrayList<Post>)[0]
            commentList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATA_TYPE_COMMENTS)
            )
            setTopView()
            commentListAdapter.setNewData(commentList)
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
        if (item.itemId == R.id.menu_post_owner_revise) {
            val intent = Intent(this@PostDetailActivity, PostWriteActivity::class.java)
                .putExtra("MODE", PostWriteActivity.VALUES.UIMODE.REVISE)
                .putExtra("postID", postInfo.postID)
                .putExtra("postImg", postInfo.postImg)
                .putExtra("contents", postInfo.contents)
                .putExtra("tags", postInfo.tags.toTypedArray())
            startActivity(intent)
        } else if (item.itemId == R.id.menu_post_owner_delete) {
            App.showToast("삭제")

            MaterialDialog(this).show {
                message(text = "삭제하시겠습니까?")
                positiveButton(text = "확인") {
                    CoroutineScope(Dispatchers.Main).launch {
                        runCatching {
                            postService.deletePost(postID)
                            finish()
                        }
                            .onSuccess {
                            }
                            .onFailure {
                                RetrofitBuilder.defaultErrorHandler(it)
                            }
                    }
                }
                negativeButton(text = "취소")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val res1 = postService.getPost(postID)
            postInfo = res1.body()!!

            val res2 = commentService.getComment(postID)
            commentList = res2.body()!!

            setTopView()
            commentListAdapter.setNewData(commentList)
            commentListAdapter.setEmptyView(
                R.layout.rv_empty_comment,
                binding.postCommentRecycler.parent as ViewGroup
            )
            if(postInfo.postID == postID){
                reviseMenu.isVisible = true
                deleteMenu.isVisible = true
            }
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    private fun setTopView() {
        binding.apply {
            ImageLoadUtil.loadPostImage(
                this@PostDetailActivity,
                postInfo.postImg,
                binding.postPostimg
            )
            ImageLoadUtil.loadUserImage(
                this@PostDetailActivity,
                postInfo.userImg,
                binding.postUserimg
            )
            postLike.setImageResource(
                if (postInfo.likes.contains(userID))
                    R.drawable.ic_favorite_red
                else
                    R.drawable.ic_favorite_black
            )

            postNickname.text = postInfo.nickname
            postContents.text = postInfo.contents
            postTime.text = Utils.convertMillisToText(postInfo.dateTime)
            postLikecount.text = postInfo.likes.size.toString()
            postCommentcount.text = postInfo.comments.size.toString()

            postUserWrapper.setOnClickListener {
                val intent = Intent(this@PostDetailActivity, HomeActivity::class.java)
                    .putExtra("userID", postInfo.userID)
                startActivity(intent)
            }
            postLike.setOnClickListener { toggleLikePost(userID, postID) }

            postTags.removeAllViews()
            for (tag in postInfo.tags) {
                val tagcontainer = layoutInflater.inflate(R.layout.v_tag_post, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_post_text)
                tagview.text = "#$tag"
                binding.postTags.addView(tagcontainer)
            }
            binding.postCommentOk.setOnClickListener { writeComment() }
        }
    }

    private fun toggleLikePost(userID: String, postID: Int) {
        val newLike: Int
        if (!postInfo.likes.contains(userID)) {
            newLike = 1
            binding.postLike.setImageResource(R.drawable.ic_favorite_black)
        } else {
            newLike = -1
            binding.postLike.setImageResource(R.drawable.ic_favorite_red)
        }

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                postService.setLikePost(userID, postID, newLike)
                loadData()
            }
                .onSuccess { }
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
    }

    private fun writeComment() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            if (binding.postCommentEdittext.text.toString() != "") {
                commentService.createComment(
                    postID, userID,
                    binding.postCommentEdittext.text.toString(), System.currentTimeMillis()
                )

                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.postCommentEdittext.windowToken, 0)
                binding.postCommentEdittext.setText("")
                loadData()
            } else App.showToast("댓글을 입력 해 주세요")
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }

    private fun removeComment(commentID: Int) = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            commentService.deleteComment(commentID)
            loadData()
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}