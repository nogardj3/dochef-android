package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainMyrecipeFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.utils.*
import java.util.*

class MyRecipeFragment : BaseFragment(), OnRefreshListener {
    // TODO
    // RecyclerView listitem Databinding
    // swipe refresh

    private lateinit var binding: MainMyrecipeFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }

    private lateinit var myRecipeListAdapter: MyRecipeListAdapter

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

            myRecipeListAdapter = MyRecipeListAdapter(this@MyRecipeFragment)
            myrecipeRecycler.adapter = myRecipeListAdapter
        }

        mainViewModel.allMyrecipeList.observe(viewLifecycleOwner, {
            binding.myrecipeEmpty.isVisible = it.isEmpty()
            myRecipeListAdapter.submitList(it) {
                binding.myrecipeRecycler.scrollToPosition(0)
            }
            binding.myrecipeSwipe.isRefreshing = false
        })

        return binding.root
    }

    override fun onRefresh() {
        binding.myrecipeSwipe.isRefreshing = true
        mainViewModel.refreshMyrecipesList()
    }

    fun goRecipeDetail(item: Recipe) {
        startActivity(
            Intent(requireContext(), RecipeDetailActivity::class.java)
                .putExtra("recipeID", item.recipeID)
        )
    }
}