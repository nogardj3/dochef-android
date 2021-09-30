package com.yhjoo.dochef.ui.follow

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.FollowlistActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class FollowListActivity : BaseActivity() {
    companion object UIMODE {
        const val FOLLOWER = 0
        const val FOLLOWING = 1
    }

    private val binding: FollowlistActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.followlist_activity)
    }
    private val followListViewModel: FollowListViewModel by viewModels {
        FollowListViewModelFactory(
            application,
            UserRepository(applicationContext),
            intent
        )
    }
    private lateinit var followListAdapter: FollowListAdapter

    private var currentMode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.followlistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentMode = intent.getIntExtra("MODE", FOLLOWER)

        val activeUserId = DatastoreUtil.getUserBrief(this).userID

        binding.apply {
            lifecycleOwner = this@FollowListActivity

            followlistToolbar.title = if (currentMode == FOLLOWER) "팔로워" else "팔로잉"

            followListAdapter = FollowListAdapter(
                activeUserId,
                { item -> followListViewModel.subscribeUser(item.userID) },
                { item -> followListViewModel.unsubscribeUser(item.userID) },
                { item ->
                    Intent(this@FollowListActivity, HomeActivity::class.java)
                        .putExtra("userID", item.userID).apply {
                            startActivity(this)
                        }
                }
            )

            followlistRecycler.apply {
                layoutManager = LinearLayoutManager(this@FollowListActivity)
                adapter = followListAdapter
            }

            followListViewModel.activeUserDetail.observe(this@FollowListActivity, {
                followListAdapter.activeUserFollowList = it.follow
                followListViewModel.requestFollowLists(currentMode)
            })
            followListViewModel.allFollowLists.observe(this@FollowListActivity, {
                followlistEmpty.isVisible = it.isEmpty()
                followListAdapter.submitList(it) {}
            })
        }
    }
}