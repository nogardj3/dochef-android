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
import com.yhjoo.dochef.databinding.FMainTimelineBinding
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.Post
import com.yhjoo.dochef.ui.activities.HomeActivity
import com.yhjoo.dochef.ui.activities.PostDetailActivity
import com.yhjoo.dochef.ui.adapter.PostListAdapter
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.RetrofitServices.PostService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainTimelineFragment : Fragment(), OnRefreshListener {
    private lateinit var binding: FMainTimelineBinding
    private lateinit var postService: PostService
    private lateinit var postListAdapter: PostListAdapter
    private lateinit var postList: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FMainTimelineBinding.inflate(layoutInflater)
        val view: View = binding.root

        postService = RetrofitBuilder.create(
            requireContext(),
            PostService::class.java
        )

        binding.apply {
            postListAdapter = PostListAdapter().apply {
                setEmptyView(
                    R.layout.rv_loading,
                    timelineRecycler.parent as ViewGroup
                )
                onItemClickListener =
                    BaseQuickAdapter.OnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
                        val intent =
                            Intent(
                                this@MainTimelineFragment.context,
                                PostDetailActivity::class.java
                            )
                                .putExtra("postID", (adapter.data[position] as Post).postID)
                        startActivity(intent)
                    }
                onItemChildClickListener =
                    BaseQuickAdapter.OnItemChildClickListener { baseQuickAdapter: BaseQuickAdapter<*, *>, view12: View, i: Int ->
                        when (view12.id) {
                            R.id.timeline_userimg, R.id.timeline_nickname -> {
                                val intent = Intent(context, HomeActivity::class.java)
                                    .putExtra("userID", (baseQuickAdapter.data[i] as Post).userID)
                                startActivity(intent)
                            }
                        }
                    }
            }

            timelineRecycler.layoutManager = LinearLayoutManager(requireContext())
            timelineRecycler.adapter = postListAdapter
        }

        return view
    }

    override fun onRefresh() {
        Handler().postDelayed({ getPostList() }, 1000)
    }

    override fun onResume() {
        super.onResume()
        if (App.isServerAlive) getPostList() else {
            postList = DataGenerator.make(resources, resources.getInteger(R.integer.DATA_TYPE_POST))
            postListAdapter.setNewData(postList)
        }
    }

    private fun getPostList() = CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            val res1 = postService.getPostList()
            postList = res1.body()!!
            postListAdapter.setNewData(postList)
            postListAdapter.setEmptyView(
                R.layout.rv_empty_post,
                binding.timelineSwipe.parent as ViewGroup
            )
            Handler().postDelayed({ binding.timelineSwipe.isRefreshing = false }, 1000)
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}