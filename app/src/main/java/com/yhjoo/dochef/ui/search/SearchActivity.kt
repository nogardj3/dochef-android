package com.yhjoo.dochef.ui.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.SearchActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.common.viewmodel.RecipeListViewModelFactory
import com.yhjoo.dochef.utils.OtherUtil

class SearchActivity : BaseActivity() {
    private val binding: SearchActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.search_activity)
    }
    val searchViewModel: SearchViewModel by viewModels() {
        SearchViewModelFactory(
            UserRepository(applicationContext),
            RecipeRepository(applicationContext)
        )
    }

    private lateinit var viewPagerAdapter: SearchViewPagerAdapter

    companion object RESULT {
        const val USER = 0
        const val RECIPE_NAME = 1
        const val INGREDIENT = 2
        const val TAG = 3
    }

    private val tabStrings = arrayOf(
        "유저",
        "레시피",
        "재료",
        "태그",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        binding.apply {
            viewPagerAdapter = SearchViewPagerAdapter(this@SearchActivity)

            searchViewpager.apply {
                offscreenPageLimit = 4
                adapter = viewPagerAdapter
            }
            TabLayoutMediator(searchTablayout, searchViewpager) { tab, position ->
                tab.text = tabStrings[position]
            }.attach()

            searchSearchview.apply {
                isSubmitButtonEnabled = true
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchViewModel.keyword.postValue(query)
                        hideKeyboard(searchSearchview)
                        return true
                    }
                })
            }
        }
    }

    class SearchViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0-> ResultUserFragment()
                1-> ResultRecipeNameFragment()
                2-> ResultIngredientFragment()
                else -> ResultTagFragment()
            }
        }
    }
}