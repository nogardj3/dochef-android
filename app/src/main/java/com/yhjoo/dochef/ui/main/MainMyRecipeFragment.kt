package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.common.adapter.RecipeListVerticalAdapter
import com.yhjoo.dochef.databinding.MainMyrecipeFragmentBinding
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModel
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModelFactory
import java.util.*

class MainMyRecipeFragment : Fragment(), OnRefreshListener {
    /* TODO
    1. ad + item + recommend
     */

    private lateinit var binding: MainMyrecipeFragmentBinding
    private lateinit var recipeListViewModel: RecipeListViewModel
    private lateinit var recipeListVerticalAdapter: RecipeListVerticalAdapter

    private lateinit var recommendTags: Array<String>
    private var userID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_myrecipe_fragment, container, false)
        val view: View = binding.root

        userID = DatastoreUtil.getUserBrief(requireContext()).userID

        val factory = RecipeListViewModelFactory(
            RecipeRepository(
                requireContext().applicationContext
            )
        )

        recipeListViewModel = factory.create(RecipeListViewModel::class.java).apply {
            allRecipeList.observe(viewLifecycleOwner, {
                recipeListVerticalAdapter.submitList(it) {}
                binding.myrecipeSwipe.isRefreshing = false
            })
        }

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
                activeUserID = userID,
                { item ->
                    val intent =
                        Intent(
                            this@MainMyRecipeFragment.requireContext(),
                            RecipeDetailActivity::class.java
                        )
                            .putExtra("recipeID", item.recipeID)
                    startActivity(intent)
                }
            )

            myrecipeRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = recipeListVerticalAdapter
            }

            recipeListViewModel.requestRecipeList(
                searchby = RecipeRepository.Companion.SEARCHBY.USERID,
                sort = RecipeListVerticalAdapter.Companion.SORT.LATEST,
                searchValue = userID
            )

            recommendTags = resources.getStringArray(R.array.recommend_tags)
        }

        return view
    }

    override fun onRefresh() {
        binding.myrecipeSwipe.isRefreshing = true
        recipeListViewModel.requestRecipeList(
            searchby = RecipeRepository.Companion.SEARCHBY.USERID,
            sort = RecipeListVerticalAdapter.Companion.SORT.LATEST,
            searchValue = userID
        )
    }
}