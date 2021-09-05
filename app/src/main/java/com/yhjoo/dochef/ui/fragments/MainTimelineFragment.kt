package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.BaseActivity
import com.yhjoo.dochef.activities.HomeActivity
import com.yhjoo.dochef.activities.PostDetailActivity
import com.yhjoo.dochef.adapter.PostListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.databinding.FMainTimelineBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.PostService
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Response
import java.util.*

class MainTimelineFragment : Fragment(), OnRefreshListener {
    var binding: FMainTimelineBinding? = null
    var postService: PostService? = null
    var postListAdapter: PostListAdapter? = null
    var postList: ArrayList<Post?>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FMainTimelineBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        postService = RxRetrofitBuilder.create(
            context,
            PostService::class.java
        )
        binding!!.timelineSwipe.setOnRefreshListener(this)
        binding!!.timelineSwipe.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
        postListAdapter = PostListAdapter()
        postListAdapter!!.setEmptyView(
            R.layout.rv_loading,
            binding!!.timelineRecycler.parent as ViewGroup
        )
        postListAdapter!!.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter: BaseQuickAdapter<*, *>, view1: View?, position: Int ->
                val intent =
                    Intent(this@MainTimelineFragment.context, PostDetailActivity::class.java)
                        .putExtra("postID", (adapter.data[position] as Post).postID)
                startActivity(intent)
            }
        postListAdapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view12: View, i: Int ->
                when (view12.id) {
                    R.id.timeline_userimg, R.id.timeline_nickname -> {
                        val intent = Intent(context, HomeActivity::class.java)
                            .putExtra("userID", (baseQuickAdapter.data[i] as Post).userID)
                        startActivity(intent)
                    }
                }
            }
        binding!!.timelineRecycler.layoutManager = LinearLayoutManager(this.context)
        binding!!.timelineRecycler.adapter = postListAdapter
        return view
    }

    override fun onRefresh() {
        Handler().postDelayed({ getPostList() }, 1000)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive()) getPostList() else {
            postList = DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_POST))
            postListAdapter!!.setNewData(postList)
        }
    }

    fun getPostList() {
        (activity as BaseActivity?).getCompositeDisposable().add(
            postService.getPostList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: Response<ArrayList<Post?>>? ->
                    postList = response!!.body()
                    postListAdapter!!.setNewData(response.body())
                    postListAdapter!!.setEmptyView(
                        R.layout.rv_empty_post,
                        binding!!.timelineSwipe.parent as ViewGroup
                    )
                    Utils.log("Finish@@")
                    Handler().postDelayed({ binding!!.timelineSwipe.isRefreshing = false }, 1000)
                }, RxRetrofitBuilder.defaultConsumer())
        )
    }
}