package com.yhjoo.dochef.ui.recipe

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.RecipemakeActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeMakeActivity : BaseActivity() {
    companion object{
        object MODE {
            const val WRITE = 0
            const val REVISE = 1
        }
    }

    private val binding: RecipemakeActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.recipemake_activity)
    }
    private val recipeMakeViewModel: RecipeMakeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipemakeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}