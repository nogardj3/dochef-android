package com.yhjoo.dochef.ui.setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.SettingActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : BaseActivity() {
    private val binding: SettingActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.setting_activity)
    }
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}