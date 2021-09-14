package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import com.yhjoo.dochef.App
import com.yhjoo.dochef.BuildConfig
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ASettingBinding
import com.yhjoo.dochef.utils.*

class SettingActivity : BaseActivity() {
    private val binding: ASettingBinding by lazy { ASettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.setting_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}