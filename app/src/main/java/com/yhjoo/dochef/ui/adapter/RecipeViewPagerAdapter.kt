package com.yhjoo.dochef.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yhjoo.dochef.model.RecipeDetail
import com.yhjoo.dochef.model.RecipePhase
import java.util.*

class RecipeViewPagerAdapter(Fm: FragmentManager?) : FragmentPagerAdapter(
    Fm!!
) {
    val fragments: MutableList<Fragment> = ArrayList()
    fun addFragment(fragment: Fragment, item: RecipePhase?) {
        val b = Bundle()
        b.putSerializable("item", item)
        fragment.arguments = b
        fragments.add(fragment)
    }

    fun addFragment(fragment: Fragment, item: RecipeDetail?) {
        val b = Bundle()
        b.putSerializable("item", item)
        fragment.arguments = b
        fragments.add(fragment)
    }

    fun addFragment(fragment: Fragment, item: RecipePhase?, item2: RecipeDetail?) {
        val b = Bundle()
        b.putSerializable("item", item)
        b.putSerializable("item2", item2)
        fragment.arguments = b
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}