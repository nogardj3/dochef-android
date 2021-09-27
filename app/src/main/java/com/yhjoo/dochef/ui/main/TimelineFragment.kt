package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.databinding.MainTimelineFragmentBinding
import com.yhjoo.dochef.ui.common.viewmodel.PostListViewModel
import com.yhjoo.dochef.ui.common.viewmodel.PostListViewModelFactory
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class TimelineFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. myfriend
    2. AD
     */
    private lateinit var binding: MainTimelineFragmentBinding
    private lateinit var timelineListViewModel: PostListViewModel
    private lateinit var timelineListAdapter: TimelineListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_timeline_fragment, container, false)
        val view: View = binding.root

        val factory = PostListViewModelFactory(
            PostRepository(
                requireContext().applicationContext
            )
        )

        timelineListViewModel = factory.create(PostListViewModel::class.java).apply {
            allPosts.observe(viewLifecycleOwner, {
                timelineListAdapter.submitList(it) {
                    binding.timelineRecycler.scrollToPosition(0)
                }
                binding.timelineSwipe.isRefreshing = false
            })
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            timelineSwipe.apply {
                setOnRefreshListener(this@TimelineFragment)
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            timelineListAdapter = TimelineListAdapter(
                { item -> userClick(item) },
                { item -> itemClick(item) }
            )

            timelineRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = timelineListAdapter
            }

            timelineListViewModel.requestPostList()
        }

        return view
    }

    override fun onRefresh() {
        binding.timelineSwipe.isRefreshing = true
        timelineListViewModel.requestPostList()
    }

    private fun userClick(post: Post) {
        val intent = Intent(context, HomeActivity::class.java)
            .putExtra("userID", post.userID)
        startActivity(intent)
    }

    private fun itemClick(post: Post) {
        val intent =
            Intent(
                this@TimelineFragment.context,
                PostDetailActivity::class.java
            )
                .putExtra("postID", post.postID)
        startActivity(intent)
    }
}