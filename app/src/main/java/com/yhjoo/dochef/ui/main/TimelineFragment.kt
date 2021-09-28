package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainTimelineFragmentBinding
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
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }
    private lateinit var timelineListAdapter: TimelineListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_timeline_fragment, container, false)
        val view: View = binding.root

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

            mainViewModel.allTimelines.observe(viewLifecycleOwner, {
                timelineListAdapter.submitList(it) {
                    binding.timelineRecycler.scrollToPosition(0)
                }
                binding.timelineSwipe.isRefreshing = false
            })

            mainViewModel.requestPostList()
        }

        return view
    }

    override fun onRefresh() {
        binding.timelineSwipe.isRefreshing = true
        mainViewModel.requestPostList()
    }

    private fun userClick(post: Post) {
        Intent(context, HomeActivity::class.java)
            .putExtra("userID", post.userID).apply {
                startActivity(this)
            }
    }

    private fun itemClick(post: Post) {
        Intent(this@TimelineFragment.context, PostDetailActivity::class.java)
            .putExtra("postID", post.postID).apply {
                startActivity(this)
            }
    }
}