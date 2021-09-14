package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.model.UserDetail
import com.yhjoo.dochef.databinding.AFollowlistBinding
import com.yhjoo.dochef.ui.adapter.FollowListAdapter
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RetrofitServices.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FollowListActivity : BaseActivity() {
    object UIMODE {
        const val FOLLOWER = 0
        const val FOLLOWING = 1
    }

    private val binding: AFollowlistBinding by lazy { AFollowlistBinding.inflate(layoutInflater) }

    private lateinit var rxUserService: UserService
    private lateinit var followListAdapter: FollowListAdapter
    private lateinit var userDetailInfo: UserDetail
    private lateinit var userList: ArrayList<UserBrief>
    private lateinit var activeUserid: String
    private lateinit var targetId: String

    private var currentMode = UIMODE.FOLLOWER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.followlistToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rxUserService = RetrofitBuilder.create(this, UserService::class.java)

        currentMode = intent.getIntExtra("MODE", UIMODE.FOLLOWER)
        activeUserid = Utils.getUserBrief(this).userID
        targetId = intent.getStringExtra("userID").toString()

        binding.apply {
            followlistToolbar.title = if (currentMode == UIMODE.FOLLOWER) "팔로워" else "팔로잉"

            followListAdapter = FollowListAdapter(activeUserid).apply {
                setEmptyView(R.layout.rv_loading, binding.followlistRecycler.parent as ViewGroup)
                setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                    Utils.log((adapter.data[position] as UserBrief).userID)
                    val intent = Intent(this@FollowListActivity, HomeActivity::class.java)
                        .putExtra("userID", (adapter.data[position] as UserBrief).userID)
                    startActivity(intent)
                }
                setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
                    onListItemClick(
                        adapter,
                        view,
                        position
                    )
                }
            }

            followlistRecycler.apply {
                layoutManager = LinearLayoutManager(this@FollowListActivity)
                adapter = followListAdapter
            }
        }

        if (App.isServerAlive)
            CoroutineScope(Dispatchers.Main).launch {
                runCatching {
                    val res1 = rxUserService.getUserDetail(activeUserid)
                    userDetailInfo = res1.body()!!

                    val res2 = if (currentMode == UIMODE.FOLLOWER)
                        rxUserService.getFollowers(targetId)
                    else
                        rxUserService.getFollowings(targetId)

                    userList = res2.body()!!
                    setListData()
                }
                    .onSuccess {}
                    .onFailure {
                        RetrofitBuilder.defaultErrorHandler(it)
                    }
            }
        else {
            userDetailInfo =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            userList =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF))
            setListData()
        }
    }

    private fun onListItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val target = (adapter.data[position] as UserBrief).userID

        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                if (view.id == R.id.user_followcancel_btn)
                    rxUserService.subscribeUser(activeUserid, target)
                else
                    rxUserService.unsubscribeUser(activeUserid, target)

                val res1 = rxUserService.getUserDetail(activeUserid)
                userDetailInfo = res1.body()!!

                val res2 =
                    if (currentMode == UIMODE.FOLLOWER)
                        rxUserService.getFollowers(targetId)
                    else
                        rxUserService.getFollowings(targetId)

                userList = res2.body()!!
                setListData()
            }
                .onSuccess { }
                .onFailure {
                    RetrofitBuilder.defaultErrorHandler(it)
                }
        }
    }

    private fun setListData() {
        followListAdapter.apply {
            settingUserFollow(userDetailInfo.follow)
            setNewData(userList)
            setEmptyView(
                R.layout.rv_empty_follower,
                binding.followlistRecycler.parent as ViewGroup
            )
        }
    }
}