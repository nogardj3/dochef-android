package com.yhjoo.dochef.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import java.util.*

class MainAdPagerAdapter(var mContext: Context?, var imgids: ArrayList<Int>) : PagerAdapter() {
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val aa = AppCompatImageView(mContext!!)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        aa.layoutParams = lp
        Glide.with(mContext!!)
            .load(imgids[position])
            .centerInside()
            .into(aa)
        collection.addView(aa)
        return aa
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return imgids.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
}