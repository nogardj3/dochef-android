package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SettingActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity

class SettingActivity : BaseActivity() {
    private val binding: SettingActivityBinding by lazy {
        SettingActivityBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.setting_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}