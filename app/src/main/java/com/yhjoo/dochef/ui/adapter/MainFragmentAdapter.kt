package com.yhjoo.dochef.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainFragmentAdapter(
    fm: FragmentManager?,
    behavior: Int,
    private val fragmentList: List<Fragment>
) : FragmentPagerAdapter(
    fm!!, behavior
) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}