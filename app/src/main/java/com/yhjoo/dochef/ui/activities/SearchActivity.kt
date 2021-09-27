package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import com.yhjoo.dochef.databinding.SearchActivityBinding
import com.yhjoo.dochef.ui.fragments.*
import com.yhjoo.dochef.utilities.Utils

class SearchActivity : BaseActivity() {
    private val binding: SearchActivityBinding by lazy {
        SearchActivityBinding.inflate(
            layoutInflater
        )
    }
    private lateinit var viewPagerAdapter: SearchViewPagerAdapter
    private val tabStrings = arrayOf(
        "유저",
        "레시피",
        "재료",
        "태그",
    )
    var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        viewPagerAdapter = SearchViewPagerAdapter(this@SearchActivity)
        binding.apply {
//            viewPagerAdapter = SearchViewPagerAdapter(supportFragmentManager).apply {
//                searchTypes.add(SearchType(1, "유저"))
//                searchTypes.add(SearchType(2, "레시피"))
//                searchTypes.add(SearchType(3, "재료"))
//                searchTypes.add(SearchType(4, "태그"))
//                for (i in searchTypes.indices) {
//                    addFragment(searchTypes[i].fragmentSearch, searchTypes[i].title)
//                }
//            }
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
                        Utils.log("submitsubmit", query!!)
                        keyword = query
//                        when (binding.searchViewpager.currentItem) {
//                            0 -> (viewPagerAdapter.getItem(0) as SearchResultFragment).search()
//                            1 -> (viewPagerAdapter.getItem(1) as SearchResultFragment).search()
//                            2 -> (viewPagerAdapter.getItem(2) as SearchResultFragment).search()
//                            3 -> (viewPagerAdapter.getItem(3) as SearchResultFragment).search()
//                        }
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
            val fragment = SearchResultFragment()
            fragment.arguments = bundleOf(
                "type" to position + 1
            )
            return when (position) {
                in 0..3 -> fragment
                else -> throw Throwable("limit ")
            }
        }
    }
}