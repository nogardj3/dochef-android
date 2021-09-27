package com.yhjoo.dochef.ui.follow

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.FollowlistActivityBinding
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class FollowListActivity : BaseActivity() {
    object UIMODE {
        const val FOLLOWER = 0
        const val FOLLOWING = 1
    }

    private val binding: FollowlistActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.followlist_activity)
    }
    private lateinit var followListViewModel: FollowListViewModel

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
        activeUserId = DatastoreUtil.getUserBrief(this).userID
        currentUserId = intent.getStringExtra("userID").toString()

        val factory = FollowListViewModelFactory(
            UserRepository(
                applicationContext,
                currentMode,
                activeUserId,
                currentUserId
            )
        )
        followListViewModel = factory.create(FollowListViewModel::class.java).apply {
            activeUserDetail.observe(this@FollowListActivity, {
                followListAdapter.activeUserFollowList = it.follow
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
                { item -> followListViewModel.subscribeUser(item.userID) },
                { item -> followListViewModel.unsubscribeUser(item.userID) },
                { item -> itemClicked(item) },
            )

            followlistRecycler.apply {
                layoutManager = LinearLayoutManager(this@FollowListActivity)
                adapter = followListAdapter
            }
        }
    }

    private fun itemClicked(userBrief: UserBrief) {
        val intent = Intent(this@FollowListActivity, HomeActivity::class.java)
            .putExtra("userID", userBrief.userID)
        startActivity(intent)
    }
}