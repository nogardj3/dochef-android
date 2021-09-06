package com.yhjoo.dochef.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.gms.ads.MobileAds
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.adapter.SearchViewPagerAdapter
import com.yhjoo.dochef.databinding.ASearchBinding
import com.yhjoo.dochef.ui.fragments.ResultFragment
import com.yhjoo.dochef.data.model.SearchType
import com.yhjoo.dochef.ui.activities.BaseActivity

class SearchActivity : BaseActivity() {
    // TODO

    lateinit var binding: ASearchBinding
    lateinit var viewPagerAdapter: SearchViewPagerAdapter
    lateinit var Types: MutableList<SearchType>
    var keyword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ASearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this)
        
        viewPagerAdapter = SearchViewPagerAdapter(supportFragmentManager)
        Types.add(SearchType(1, "유저"))
        Types.add(SearchType(2, "레시피"))
        Types.add(SearchType(3, "재료"))
        Types.add(SearchType(4, "태그"))
        for (i in Types.indices) {
            viewPagerAdapter.addFragment(Types[i].fragment, Types[i].title)
        }
        binding.searchViewpager.adapter = viewPagerAdapter
        binding.searchViewpager.offscreenPageLimit = 4
        binding.searchEdittext.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = binding.searchEdittext.text.toString()
                (viewPagerAdapter.getItem(binding.searchViewpager.currentItem) as ResultFragment).search()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchEdittext.windowToken, 0)
            }
            true
        }
        binding.searchTablayout.setupWithViewPager(binding.searchViewpager)
        binding.searchBtnSearch.setOnClickListener { v: View? -> onClickSearch(v) }
        binding.searchBtnBack.setOnClickListener { v: View? -> onClickBack(v) }
    }

    fun onClickSearch(v: View?) {
        if (binding.searchEdittext.text.toString().trim { it <= ' ' }.isEmpty()) {
            appInstance.showToast("한 글자 이상 입력해주세요.")
        } else {
            keyword = binding.searchEdittext.text.toString().trim { it <= ' ' }
            binding.searchEdittext.setText(keyword)
            when (binding.searchViewpager.currentItem) {
                0 -> (viewPagerAdapter.getItem(0) as ResultFragment).search()
                1 -> (viewPagerAdapter.getItem(1) as ResultFragment).search()
                2 -> (viewPagerAdapter.getItem(2) as ResultFragment).search()
                3 -> (viewPagerAdapter.getItem(3) as ResultFragment).search()
            }
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchEdittext.windowToken, 0)
        }
    }

    fun onClickBack(v: View?) {
        finish()
    }
}