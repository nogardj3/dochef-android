package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.FollowListAdapter
import com.yhjoo.dochef.databinding.FollowlistActivityBinding
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.repository.FollowListRepository
import com.yhjoo.dochef.utilities.*
import com.yhjoo.dochef.viewmodel.FollowListViewModel
import com.yhjoo.dochef.viewmodel.FollowListViewModelFactory
import java.util.*

class FollowListActivity : BaseActivity() {
    object UIMODE {
        const val FOLLOWER = 0
        const val FOLLOWING = 1
    }

    private val binding: FollowlistActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.followlist_activity)
    }

    private lateinit var followlistViewModel: FollowListViewModel

    private lateinit var followListAdapter: FollowListAdapter
    private lateinit var activeUserId: String
    private lateinit var currentUserId: String

    private var currentMode = UIMODE.FOLLOWER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.followlistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentMode = intent.getIntExtra("MODE", UIMODE.FOLLOWER)
        activeUserId = Utils.getUserBrief(this).userID
        currentUserId = intent.getStringExtra("userID").toString()

        val factory = FollowListViewModelFactory(
            FollowListRepository(
                applicationContext,
                currentMode,
                activeUserId,
                currentUserId
            )
        )
        followlistViewModel = factory.create(FollowListViewModel::class.java).apply {
            activeUserDetail.observe(this@FollowListActivity, {
                followListAdapter.activeUserFollowList = it.follow
                followListAdapter.notifyDataSetChanged()
            })
            allFollowLists.observe(this@FollowListActivity, {
                followListAdapter.submitList(it) {
                    binding.followlistRecycler.scrollToPosition(0)
                }
            })
        }

        binding.apply {
            lifecycleOwner = this@FollowListActivity

            followlistToolbar.title = if (currentMode == UIMODE.FOLLOWER) "팔로워" else "팔로잉"

            followListAdapter = FollowListAdapter(
                activeUserId,
                { item -> followlistViewModel.subscribeUser(item.userID) },
                { item -> followlistViewModel.unsubscribeUser(item.userID) },
                { item -> itemClicked(item) },
            )

            followlistRecycler.apply {
                layoutManager = LinearLayoutManager(this@FollowListActivity)
                adapter = followListAdapter
            }
        }
    }

    private fun itemClicked(userBrief: UserBrief): Unit {
        val intent = Intent(this@FollowListActivity, HomeActivity::class.java)
            .putExtra("userID", userBrief.userID)
        startActivity(intent)
    }
}