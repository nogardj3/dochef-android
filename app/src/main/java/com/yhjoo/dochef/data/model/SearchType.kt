package com.yhjoo.dochef.data.model

import android.os.Bundle
import com.yhjoo.dochef.ui.fragments.ResultFragment

class SearchType(type: Int, var title: String) {
    var fragment: ResultFragment

    init {
        fragment = ResultFragment()
        val b = Bundle()
        b.putInt("type", type)
        fragment.arguments = b
    }
}