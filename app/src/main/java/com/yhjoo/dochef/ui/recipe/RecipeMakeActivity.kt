package com.yhjoo.dochef.ui.recipe

import android.os.Bundle
import com.yhjoo.dochef.databinding.RecipemakeActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity

class RecipeMakeActivity : BaseActivity() {
    /* TODO
    ALL, REVISE 포함
     */

    object MODE {
        const val WRITE = 0
        const val REVISE = 1
    }

    private val binding: RecipemakeActivityBinding by lazy {
        RecipemakeActivityBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.recipemakeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}