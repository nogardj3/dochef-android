package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.adapter.PostListAdapter
import com.yhjoo.dochef.databinding.MainTimelineFragmentBinding
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.ui.HomeActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.ui.viewmodel.PostListViewModel
import com.yhjoo.dochef.ui.viewmodel.PostListViewModelFactory
import java.util.*

class MainTimelineFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. myfriend
    2. AD
     */
    private lateinit var binding: MainTimelineFragmentBinding
    private lateinit var postlistViewModel: PostListViewModel
    private lateinit var postListAdapter: PostListAdapter

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

        postlistViewModel = factory.create(PostListViewModel::class.java).apply {
            allPostList.observe(viewLifecycleOwner, {
                postListAdapter.submitList(it) {
                    binding.timelineRecycler.scrollToPosition(0)
                }
                binding.timelineSwipe.isRefreshing = false
            })
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            timelineSwipe.apply {
                setOnRefreshListener(this@MainTimelineFragment)
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            postListAdapter = PostListAdapter(
                PostListAdapter.MAIN_TIMELINE, // TODO companion object
                { item -> userClick(item) },
                { item -> itemClick(item) }
            )

            timelineRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = postListAdapter
            }

            postlistViewModel.requestPostList()
        }

        return view
    }

    override fun onRefresh() {
        binding.timelineSwipe.isRefreshing = true
        postlistViewModel.requestPostList()
    }

    private fun userClick(post: Post) {
        val intent = Intent(context, HomeActivity::class.java)
            .putExtra("userID", post.userID)
        startActivity(intent)
    }

    private fun itemClick(post: Post) {
        val intent =
            Intent(
                this@MainTimelineFragment.context,
                PostDetailActivity::class.java
            )
                .putExtra("postID", post.postID)
        startActivity(intent)
    }
}