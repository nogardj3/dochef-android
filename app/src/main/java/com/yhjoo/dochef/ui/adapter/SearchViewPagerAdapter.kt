package com.yhjoo.dochef.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class SearchViewPagerAdapter(Fm: FragmentManager?) : FragmentPagerAdapter(
    Fm!!
) {
    var fragments: MutableList<Fragment> = ArrayList()
    var fragmentTitles: MutableList<String?> = ArrayList()
    fun addFragment(fragment: Fragment?, title: String?) {
        fragments.add(fragment!!)
        fragmentTitles.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles[position]
    }
}