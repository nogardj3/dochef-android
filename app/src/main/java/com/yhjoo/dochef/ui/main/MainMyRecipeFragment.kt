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
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainMyrecipeFragmentBinding
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class MainMyRecipeFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. ad + item + recommend
     */

    private lateinit var binding: MainMyrecipeFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            UserRepository(
                requireContext().applicationContext
            ),
            RecipeRepository(
                requireContext().applicationContext
            ),
            PostRepository(
                requireContext().applicationContext
            )
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
        val view: View = binding.root

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            myrecipeSwipe.apply {
                setOnRefreshListener(this@MainMyRecipeFragment)
                setColorSchemeColors(
                    resources.getColor(
                        R.color.colorPrimary,
                        null
                    )
                )
            }

            recipeListVerticalAdapter = RecipeListVerticalAdapter(
                RecipeListVerticalAdapter.MAIN_MYRECIPE,
                activeUserID = mainViewModel.userId.value
            ) { item ->
                Intent(
                    this@MainMyRecipeFragment.requireContext(),
                    RecipeDetailActivity::class.java
                )
                    .putExtra("recipeID", item.recipeID).apply {
                        startActivity(this)
                    }
            }

            myrecipeRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recipeListVerticalAdapter
            }

            mainViewModel.userId.observe(viewLifecycleOwner, {
                if (it != null)
                    mainViewModel.refreshMyrecipesList()
            })

            mainViewModel.allMyrecipeList.observe(viewLifecycleOwner, {
                recipeListVerticalAdapter.submitList(it) {
                    binding.myrecipeRecycler.scrollToPosition(0)
                }
                binding.myrecipeSwipe.isRefreshing = false
            })

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return view
    }

    override fun onRefresh() {
        binding.myrecipeSwipe.isRefreshing = true
        mainViewModel.refreshMyrecipesList()
    }
}