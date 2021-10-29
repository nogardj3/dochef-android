package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainTimelineFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class TimelineFragment : BaseFragment() {
    // TODO
    // swipe refresh

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

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            timelineSwipe.also {
                it.setOnRefreshListener {
                    mainViewModel.refreshPostList()
                }
                it.setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            timelineListAdapter = TimelineListAdapter(this@TimelineFragment)
            timelineRecycler.adapter = timelineListAdapter
        }

        mainViewModel.allTimelines.observe(viewLifecycleOwner, {
            binding.timelineEmpty.isVisible = it.isEmpty()
            timelineListAdapter.submitList(it) {
                binding.timelineRecycler.scrollToPosition(0)
            }
            binding.timelineSwipe.isRefreshing = false
        })

        return binding.root
    }

    fun goHome(post: Post) {
        startActivity(
            Intent(requireContext(), HomeActivity::class.java)
                .putExtra(Constants.INTENTNAME.USER_ID, post.userID)
        )
    }

    fun goPostDetail(post: Post) {
        startActivity(
            Intent(requireContext(), PostDetailActivity::class.java)
                .putExtra(Constants.INTENTNAME.POST_ID, post.postID)
        )
    }
}