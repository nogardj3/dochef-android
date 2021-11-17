package com.yhjoo.dochef.ui.follow

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.databinding.FollowlistActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FollowListActivity : BaseActivity() {
    private val binding: FollowlistActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.followlist_activity)
    }
    private val followListViewModel: FollowListViewModel by viewModels()
    private lateinit var followListAdapter: FollowListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.followlistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@FollowListActivity

            followlistToolbar.title = followListViewModel.title

            followListAdapter = FollowListAdapter(
                this@FollowListActivity,
                followListViewModel
            )

            followlistRecycler.adapter = followListAdapter
        }

        followListViewModel.activeUserDetail.observe(this@FollowListActivity, {
            followListAdapter.activeUserFollowList = it.follow
        })
        followListViewModel.allFollowLists.observe(this@FollowListActivity, {
            binding.followlistEmpty.isVisible = it.isEmpty()
            followListAdapter.submitList(it) {}
        })
    }

    fun goHome(item: UserBrief) {
        startActivity(
            Intent(this@FollowListActivity, HomeActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, item.userID)
        )
    }

    companion object {
        const val FOLLOWER = 0
        const val FOLLOWING = 1
    }
}