package com.yhjoo.dochef.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.BuildConfig
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ASettingBinding
import com.yhjoo.dochef.ui.activities.AccountActivity
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.ui.activities.FAQActivity
import com.yhjoo.dochef.utils.*

class SettingActivity : BaseActivity() {
    lateinit var binding: ASettingBinding
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var notiSettingArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ASettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        notiSettingArray = resources.getStringArray(R.array.sp_noti)

        binding.settingNotice.setOnClickListener { startNotice() }
        binding.settingVersion.text = BuildConfig.VERSION_NAME
        binding.settingFaq.setOnClickListener { startFAQ() }
        binding.settingTos.setOnClickListener { startTOS() }
        binding.settingLogout.setOnClickListener { signOut() }
        binding.settingNotificationAllCheck.setOnClickListener {
            toggleAllnotification()
        }
        binding.settingNotification0Check.setOnClickListener {
            toggleNotification(0,null)
        }
        binding.settingNotification1Check.setOnClickListener {
            toggleNotification(1,null)
        }
        binding.settingNotification2Check.setOnClickListener {
            toggleNotification(2,null)
        }
        binding.settingNotification3Check.setOnClickListener {
            toggleNotification(3,null)
        }
        binding.settingNotification4Check.setOnClickListener {
            toggleNotification(4,null)
        }
        notiSettings
    }

    private fun startNotice() {
        startActivity(Intent(this@SettingActivity, NoticeActivity::class.java))
    }

    private fun startFAQ() {
        startActivity(Intent(this@SettingActivity, FAQActivity::class.java))
    }

    private fun startTOS() {
        startActivity(Intent(this@SettingActivity, TOSActivity::class.java))
    }

    private fun signOut() {
        appInstance.showToast("로그아웃")
        ChefAuth.logOut(this)
        val intent = Intent(this, AccountActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private val notiSettings: Unit
        get() {
            binding.settingNotificationAllCheck.isChecked = false
            binding.settingNotification0Check.isChecked =
                mSharedPreferences.getBoolean(notiSettingArray[0], true)
            binding.settingNotification1Check.isChecked =
                mSharedPreferences.getBoolean(notiSettingArray[1], true)
            binding.settingNotification2Check.isChecked =
                mSharedPreferences.getBoolean(notiSettingArray[2], true)
            binding.settingNotification3Check.isChecked =
                mSharedPreferences.getBoolean(notiSettingArray[3], true)
            binding.settingNotification4Check.isChecked =
                mSharedPreferences.getBoolean(notiSettingArray[4], true)
        }

    private fun toggleAllnotification() {
        toggleNotification(0, binding.settingNotificationAllCheck.isChecked)
        toggleNotification(1, binding.settingNotificationAllCheck.isChecked)
        toggleNotification(2, binding.settingNotificationAllCheck.isChecked)
        toggleNotification(3, binding.settingNotificationAllCheck.isChecked)
        toggleNotification(4, binding.settingNotificationAllCheck.isChecked)
    }

    private fun toggleNotification(position: Int, check: Boolean?) {
        val target: AppCompatCheckBox = when (position) {
            0 -> binding.settingNotification0Check
            1 -> binding.settingNotification1Check
            2 -> binding.settingNotification2Check
            3 -> binding.settingNotification3Check
            4 -> binding.settingNotification4Check
            else -> throw IllegalStateException("Unexpected value: $position")
        }
        if (check != null) {
            target.isChecked = check
        }
        Utils.log(target.isChecked)

        mSharedPreferences.edit {
            putBoolean(notiSettingArray[position], target.isChecked)
            apply()
        }
    }
}