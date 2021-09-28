package com.yhjoo.dochef.ui.recipe.play

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.model.RecipePhase
import java.util.*

class RecipePlayViewPagerAdapter(Fm: FragmentManager?) : FragmentPagerAdapter(
    Fm!!
) {
    private val fragments: MutableList<Fragment> = ArrayList()
    fun addFragment(fragment: Fragment, item: RecipePhase?) {
        fragment.arguments = bundleOf(
            Pair("item", item)
        )
        fragments.add(fragment)
    }

    fun addFragment(fragment: Fragment, item: RecipeDetail?) {
        fragment.arguments = bundleOf(
            Pair("item", item)
        )
        fragments.add(fragment)
    }

    fun addFragment(fragment: Fragment, item: RecipePhase?, item2: RecipeDetail?) {
        fragment.arguments = bundleOf(
            Pair("item", item),
            Pair("item2", item2)
        )
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}