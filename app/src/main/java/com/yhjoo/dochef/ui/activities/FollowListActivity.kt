package com.yhjoo.dochef.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.FollowListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.databinding.AFollowlistBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.UserService
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import retrofit2.Response
import java.util.*

class FollowListActivity : BaseActivity() {
    enum class MODE {
        FOLLOWER, FOLLOWING
    }

    var binding: AFollowlistBinding? = null
    var rxUserService: UserService? = null
    var followListAdapter: FollowListAdapter? = null
    var current_mode: MODE? = MODE.FOLLOWER
    var userDetailInfo: UserDetail? = null
    var userList: ArrayList<UserBrief?>? = null
    var active_userid: String? = null
    var target_id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AFollowlistBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.followlistToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        rxUserService = RxRetrofitBuilder.create(this, UserService::class.java)
        active_userid = Utils.getUserBrief(this).userID
        target_id = intent.getStringExtra("userID")
        current_mode = intent.getSerializableExtra("MODE") as MODE?
        binding!!.followlistToolbar.title = if (current_mode == MODE.FOLLOWER) "팔로워" else "팔로잉"
        followListAdapter = FollowListAdapter(active_userid)
        followListAdapter!!.setEmptyView(
            R.layout.rv_loading,
            binding!!.followlistRecycler.parent as ViewGroup
        )
        followListAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
            Utils.log((adapter.data[position] as UserBrief).userID)
            val intent = Intent(this@FollowListActivity, HomeActivity::class.java)
                .putExtra("userID", (adapter.data[position] as UserBrief).userID)
            startActivity(intent)
        }
        followListAdapter!!.setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            onListItemClick(
                adapter,
                view,
                position
            )
        }
        binding!!.followlistRecycler.layoutManager = LinearLayoutManager(this)
        binding!!.followlistRecycler.adapter = followListAdapter
        if (App.isServerAlive()) {
            val modeSingle: Single<Response<ArrayList<UserBrief?>?>?>
            modeSingle = if (current_mode == MODE.FOLLOWER) rxUserService!!.getFollowers(target_id)
                .observeOn(AndroidSchedulers.mainThread()) else rxUserService!!.getFollowings(
                target_id
            )
                .observeOn(AndroidSchedulers.mainThread())
            compositeDisposable!!.add(
                rxUserService!!.getUserDetail(active_userid)
                    .flatMap(
                        Function { response: Response<UserDetail?>? ->
                            userDetailInfo = response!!.body()
                            modeSingle
                        } as Function<Response<UserDetail?>?, Single<Response<ArrayList<UserBrief?>?>?>>
                    )
                    .subscribe({ response: Response<ArrayList<UserBrief?>?>? ->
                        userList = response!!.body()
                        setListData()
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        } else {
            userDetailInfo =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
            userList =
                DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF))
            setListData()
        }
    }

    fun onListItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val target = (adapter.data[position] as UserBrief).userID
        val subORunsub = if (view.id == R.id.user_followcancel_btn) rxUserService!!.subscribeUser(
            active_userid,
            target
        ) else rxUserService!!.unsubscribeUser(active_userid, target)
        val modeSingle = if (current_mode == MODE.FOLLOWER) rxUserService!!.getFollowers(target_id)
            .observeOn(AndroidSchedulers.mainThread()) else rxUserService!!.getFollowings(target_id)
            .observeOn(AndroidSchedulers.mainThread())
        compositeDisposable!!.add(
            subORunsub
                .flatMap(Function { response: Response<JsonObject?>? ->
                    rxUserService!!.getUserDetail(
                        active_userid
                    )
                } as Function<Response<JsonObject?>?, Single<Response<UserDetail?>?>?>)
                .flatMap(
                    Function { response: Response<UserDetail?>? ->
                        userDetailInfo = response!!.body()
                        modeSingle
                    } as Function<Response<UserDetail?>?, Single<Response<ArrayList<UserBrief?>?>?>>
                )
                .subscribe({ response: Response<ArrayList<UserBrief?>?>? ->
                    userList = response!!.body()
                    setListData()
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }

    fun setListData() {
        followListAdapter!!.setActiveUserFollow(userDetailInfo.getFollow())
        followListAdapter!!.setNewData(userList)
        followListAdapter!!.setEmptyView(
            R.layout.rv_empty_follower,
            binding!!.followlistRecycler.parent as ViewGroup
        )
    }
}