package com.yhjoo.dochef.data.model

import android.os.Bundle
import com.yhjoo.dochef.ui.fragments.SearchResultFragment

class SearchType(type: Int, var title: String) {
    var fragmentSearch: SearchResultFragment

    init {
        fragmentSearch = SearchResultFragment()
        val b = Bundle()
        b.putInt("type", type)
        fragmentSearch.arguments = b
    }
}