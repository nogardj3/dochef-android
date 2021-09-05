package com.yhjoo.dochef.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.JsonObject
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.App
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.CommentListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.databinding.APostdetailBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.CommentService
import com.yhjoo.dochef.utils.RxRetrofitServices.PostService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import retrofit2.Response
import java.util.*

class PostDetailActivity : BaseActivity() {
    var binding: APostdetailBinding? = null
    var postService: PostService? = null
    var commentService: CommentService? = null
    var commentListAdapter: CommentListAdapter? = null
    var commentList: ArrayList<Comment?>? = null
    var postInfo: Post? = null
    var userID: String? = null
    var postID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = APostdetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.postToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        postService = RxRetrofitBuilder.create(this, PostService::class.java)
        commentService = RxRetrofitBuilder.create(this, CommentService::class.java)
        userID = Utils.getUserBrief(this).userID
        postID = intent.getIntExtra("postID", -1)
        commentListAdapter = CommentListAdapter(userID)
        commentListAdapter!!.setOnItemChildClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
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
                OnMenuItemClickListener { pos: Int, item: PowerMenuItem? ->
                    if (pos == 0) {
                        BaseActivity.Companion.createConfirmDialog(this,
                            null,
                            "삭제 하시겠습니까?",
                            SingleButtonCallback { dialog1: MaterialDialog?, which: DialogAction? ->
                                removeComment((baseQuickAdapter.getItem(position) as Comment?).getCommentID())
                            })
                            .show()
                        powerMenu.dismiss()
                    }
                }
            powerMenu.showAsAnchorCenter(view)
        }
        binding!!.postCommentRecycler.layoutManager = LinearLayoutManager(this)
        binding!!.postCommentRecycler.adapter = commentListAdapter
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) loadData() else {
            postInfo = (DataGenerator.make<Any>(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_POST)
            ) as ArrayList<Post?>)[0]
            commentList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATA_TYPE_COMMENTS)
            )
            setTopView()
            commentListAdapter!!.setNewData(commentList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (postInfo != null && postInfo.getUserID() == userID) menuInflater.inflate(
            R.menu.menu_post_owner,
            menu
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_post_owner_revise) {
            val intent = Intent(this@PostDetailActivity, PostWriteActivity::class.java)
                .putExtra("MODE", PostWriteActivity.MODE.REVISE)
                .putExtra("postID", postInfo.getPostID())
                .putExtra("postImg", postInfo.getPostImg())
                .putExtra("contents", postInfo.getContents())
                .putExtra("tags", postInfo.getTags().toTypedArray())
            startActivity(intent)
        } else if (item.itemId == R.id.menu_post_owner_delete) {
            appInstance!!.showToast("삭제")
            BaseActivity.Companion.createConfirmDialog(this,
                null, "삭제하시겠습니까?",
                SingleButtonCallback { dialog1: MaterialDialog?, which: DialogAction? ->
                    compositeDisposable!!.add(
                        postService!!.deletePost(postID)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { response: Response<JsonObject?>? -> finish() },
                                RxRetrofitBuilder.defaultConsumer())
                    )
                }).show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadData() {
        compositeDisposable!!.add(
            postService!!.getPost(postID)
                .flatMap(
                    Function { response: Response<Post?>? ->
                        postInfo = response!!.body()
                        commentService!!.getComment(postID)
                            .observeOn(AndroidSchedulers.mainThread())
                    } as Function<Response<Post?>?, Single<Response<ArrayList<Comment?>?>?>>
                )
                .subscribe({ response: Response<ArrayList<Comment?>?>? ->
                    commentList = response!!.body()
                    setTopView()
                    commentListAdapter!!.setNewData(commentList)
                    commentListAdapter!!.setEmptyView(
                        R.layout.rv_empty_comment,
                        binding!!.postCommentRecycler.parent as ViewGroup
                    )
                    invalidateOptionsMenu()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }

    fun setTopView() {
        ImageLoadUtil.loadPostImage(this, postInfo.getPostImg(), binding!!.postPostimg)
        ImageLoadUtil.loadUserImage(this, postInfo.getUserImg(), binding!!.postUserimg)
        binding!!.postNickname.text = postInfo.getNickname()
        binding!!.postContents.text = postInfo.getContents()
        binding!!.postTime.text = Utils.convertMillisToText(postInfo.getDateTime())
        binding!!.postLikecount.text = Integer.toString(postInfo.getLikes().size)
        binding!!.postCommentcount.text = Integer.toString(postInfo.getComments().size)
        binding!!.postUserWrapper.setOnClickListener { v: View? ->
            val intent = Intent(this@PostDetailActivity, HomeActivity::class.java)
                .putExtra("userID", postInfo.getUserID())
            startActivity(intent)
        }
        if (postInfo.getLikes()
                .contains(userID)
        ) binding!!.postLike.setImageResource(R.drawable.ic_favorite_red) else binding!!.postLike.setImageResource(
            R.drawable.ic_favorite_black
        )
        binding!!.postLike.setOnClickListener { v: View? -> toggleLikePost(userID, postID) }
        binding!!.postTags.removeAllViews()
        for (tag in postInfo.getTags()) {
            val tagcontainer = layoutInflater.inflate(R.layout.v_tag_post, null) as LinearLayout
            val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.vtag_post_text)
            tagview.text = "#$tag"
            binding!!.postTags.addView(tagcontainer)
        }
        binding!!.postCommentOk.setOnClickListener { v: View? -> writeComment(v) }
    }

    fun toggleLikePost(userID: String?, postID: Int) {
        val new_like: Int
        if (!postInfo.getLikes().contains(userID)) {
            new_like = 1
            binding!!.postLike.setImageResource(R.drawable.ic_favorite_black)
        } else {
            new_like = -1
            binding!!.postLike.setImageResource(R.drawable.ic_favorite_red)
        }
        compositeDisposable!!.add(
            postService!!.setLikePost(userID, postID, new_like)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: Response<JsonObject?>? -> loadData() },
                    RxRetrofitBuilder.defaultConsumer()
                )
        )
    }

    fun writeComment(v: View?) {
        if (binding!!.postCommentEdittext.text.toString() != "") {
            compositeDisposable!!.add(
                commentService!!.createComment(
                    postID, userID,
                    binding!!.postCommentEdittext.text.toString(), System.currentTimeMillis()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<JsonObject?>? ->
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding!!.postCommentEdittext.windowToken, 0)
                        binding!!.postCommentEdittext.setText("")
                        loadData()
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else appInstance!!.showToast("댓글을 입력 해 주세요")
    }

    fun removeComment(commentID: Int) {
        compositeDisposable!!.add(
            commentService!!.deleteComment(commentID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: Response<JsonObject?>? -> loadData() },
                    RxRetrofitBuilder.defaultConsumer()
                )
        )
    }
}