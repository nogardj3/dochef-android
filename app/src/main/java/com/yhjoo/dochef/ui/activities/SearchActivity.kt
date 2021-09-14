package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.gms.ads.MobileAds
import com.yhjoo.dochef.App
import com.yhjoo.dochef.ui.adapter.SearchViewPagerAdapter
import com.yhjoo.dochef.data.model.SearchType
import com.yhjoo.dochef.databinding.ASearchBinding
import com.yhjoo.dochef.ui.fragments.SearchResultFragment

class SearchActivity : BaseActivity() {
    private val binding: ASearchBinding by lazy { ASearchBinding.inflate(layoutInflater) }
    private lateinit var viewPagerAdapter: SearchViewPagerAdapter
    private lateinit var searchTypes: MutableList<SearchType>

    companion object{
        var keyword: String? = null

        fun searchKeyword():String {
            return keyword!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MobileAds.initialize(this)

        binding.apply {
            viewPagerAdapter = SearchViewPagerAdapter(supportFragmentManager).apply {
                searchTypes.add(SearchType(1, "유저"))
                searchTypes.add(SearchType(2, "레시피"))
                searchTypes.add(SearchType(3, "재료"))
                searchTypes.add(SearchType(4, "태그"))
                for (i in searchTypes.indices) {
                    viewPagerAdapter.addFragment(searchTypes[i].fragmentSearch, searchTypes[i].title)
                }
            }
            searchViewpager.adapter = viewPagerAdapter
            searchViewpager.offscreenPageLimit = 4
            searchEdittext.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = searchEdittext.text.toString()
                    (viewPagerAdapter.getItem(searchViewpager.currentItem) as SearchResultFragment).search()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchEdittext.windowToken, 0)
                }
                true
            }
            searchTablayout.setupWithViewPager(searchViewpager)
            searchBtnSearch.setOnClickListener { onClickSearch() }
            searchBtnBack.setOnClickListener { finish() }
        }
    }

    private fun onClickSearch() {
        if (binding.searchEdittext.text.toString().trim { it <= ' ' }.isEmpty()) {
            App.showToast("한 글자 이상 입력해주세요.")
        } else {
            keyword = binding.searchEdittext.text.toString().trim { it <= ' ' }
            binding.searchEdittext.setText(keyword)
            when (binding.searchViewpager.currentItem) {
                0 -> (viewPagerAdapter.getItem(0) as SearchResultFragment).search()
                1 -> (viewPagerAdapter.getItem(1) as SearchResultFragment).search()
                2 -> (viewPagerAdapter.getItem(2) as SearchResultFragment).search()
                3 -> (viewPagerAdapter.getItem(3) as SearchResultFragment).search()
            }
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEdittext.windowToken, 0)
        }
    }
}