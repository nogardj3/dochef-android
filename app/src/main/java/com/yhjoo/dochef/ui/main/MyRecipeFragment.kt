package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainMyrecipeFragmentBinding
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter.CONSTANTS.LayoutType.MAIN_MYRECIPE
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class MyRecipeFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. ad + item + recommend
     */

    private lateinit var binding: MainMyrecipeFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            requireActivity().application,
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var recommendTags: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_myrecipe_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            myrecipeSwipe.apply {
                setOnRefreshListener(this@MyRecipeFragment)
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                MAIN_MYRECIPE,
                mainViewModel.userId,
                itemClickListener = { item ->
                    Intent(requireContext(), RecipeDetailActivity::class.java)
                        .putExtra("recipeID", item.recipeID).apply {
                            startActivity(this)
                        }
                },
                null
            )

            myrecipeRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recipeListVerticalAdapter
            }

            mainViewModel.allMyrecipeList.observe(viewLifecycleOwner, {
                myrecipeEmpty.isVisible = it.isEmpty()
                recipeListVerticalAdapter.submitList(it) {
                    binding.myrecipeRecycler.scrollToPosition(0)
                }
                binding.myrecipeSwipe.isRefreshing = false
            })

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return binding.root
    }

    override fun onRefresh() {
        binding.myrecipeSwipe.isRefreshing = true
        mainViewModel.refreshMyrecipesList()
    }
}