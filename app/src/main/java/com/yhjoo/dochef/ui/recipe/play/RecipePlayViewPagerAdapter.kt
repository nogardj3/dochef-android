package com.yhjoo.dochef.ui.recipe.play

import android.os.Bundle
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