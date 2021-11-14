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
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.databinding.PostdetailActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class PostDetailActivity : BaseActivity() {
    // TODO
    // menu data binding

    private val binding: PostdetailActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.postdetail_activity)
    }
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    private lateinit var commentListAdapter: CommentListAdapter

    private var reviseMenu: MenuItem? = null
    private var deleteMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.postToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@PostDetailActivity
            activity = this@PostDetailActivity
            viewModel = postDetailViewModel
            activeUserId = App.activeUserId

            commentListAdapter = CommentListAdapter(this@PostDetailActivity)
            postCommentRecycler.adapter = commentListAdapter

            postCommentEdittext.addTextChangedListener(commentEdittextWatcher)
        }

        postDetailViewModel.postDetail.observe(this@PostDetailActivity, {
            if (it.userID == App.activeUserId) {
                lifecycleScope.launch {
                    delay(300)
                    reviseMenu?.isVisible = true
                    deleteMenu?.isVisible = true
                }
            }

            binding.postTags.removeAllViews()
            for (tag in it.tags) {
                val tagcontainer =
                    layoutInflater.inflate(R.layout.view_tag_post, null) as LinearLayout
                val tagview: AppCompatTextView = tagcontainer.findViewById(R.id.tag_post_text)
                tagview.text = "#$tag"
                binding.postTags.addView(tagcontainer)
            }
        })

        postDetailViewModel.allComments.observe(this@PostDetailActivity, {
            binding.postCommentEmpty.isVisible = it.isEmpty()
            commentListAdapter.submitList(it) {
                binding.postCommentRecycler.scrollToPosition(0)
            }
        })

        subscribeEventOnLifecycle {
            postDetailViewModel.eventResult.collect {
                when (it.first) {
                    PostDetailViewModel.Events.IS_DELETED -> {
                        App.showToast("삭제되었습니다.")
                        finish()
                    }
                }
            }
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
                startActivity(
                    Intent(this@PostDetailActivity, PostWriteActivity::class.java)
                        .putExtra("mode", PostWriteActivity.Companion.UIMODE.REVISE)
                        .putExtra("post", postInfo)
                )
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

    fun commentClick(view: View, item: Comment) {
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
        powerMenu.onMenuItemClickListener = OnMenuItemClickListener { pos, _ ->
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

    fun commentAddOk() {
        if (binding.postCommentEdittext.text.toString() != "") {
            postDetailViewModel.createComment(binding.postCommentEdittext.text.toString())
            hideKeyboard(binding.postCommentEdittext)
            binding.postCommentEdittext.setText("")
        }
    }

    fun goHome(item: Post) {
        startActivity(
            Intent(this@PostDetailActivity, HomeActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
        )
    }

    private val commentEdittextWatcher: TextWatcher = object : TextWatcher {
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
    }
}