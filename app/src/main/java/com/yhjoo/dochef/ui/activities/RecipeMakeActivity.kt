package com.yhjoo.dochef.activities

import android.os.Bundle
import com.yhjoo.dochef.databinding.ARecipemakeBinding

class RecipeMakeActivity : BaseActivity() {
    object MODE{
        const val WRITE = 0
        const val REVISE = 1
    }

    var binding: ARecipemakeBinding? = null

    /*
        TODO
        모든 기능
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ARecipemakeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.recipemakeToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}