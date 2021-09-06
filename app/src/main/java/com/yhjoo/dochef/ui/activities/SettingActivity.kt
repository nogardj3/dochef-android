package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.edit
import com.yhjoo.dochef.App
import com.yhjoo.dochef.BuildConfig
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ASettingBinding
import com.yhjoo.dochef.utils.*

class SettingActivity : BaseActivity() {
    private val binding: ASettingBinding by lazy { ASettingBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notiSettingArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        sharedPreferences = Utils.getSharedPreferences(this)
        notiSettingArray = resources.getStringArray(R.array.sp_noti)

        binding.apply {
            settingNotice.setOnClickListener { startNotice() }
            settingVersion.text = BuildConfig.VERSION_NAME
            settingFaq.setOnClickListener { startFAQ() }
            settingTos.setOnClickListener { startTOS() }
            settingLogout.setOnClickListener { signOut() }

            settingNotificationAllCheck.setOnClickListener { toggleAllnotification() }
            settingNotification0Check.setOnClickListener {
                toggleNotification(0, null)
            }
            settingNotification1Check.setOnClickListener {
                toggleNotification(1, null)
            }
            settingNotification2Check.setOnClickListener {
                toggleNotification(2, null)
            }
            settingNotification3Check.setOnClickListener {
                toggleNotification(3, null)
            }
            settingNotification4Check.setOnClickListener {
                toggleNotification(4, null)
            }
        }

        setNotificiationSettings()
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
        App.showToast("로그아웃")
        ChefAuth.logOut(this)

        startActivity(
            Intent(this, AccountActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun setNotificiationSettings() {
        binding.apply {
            settingNotificationAllCheck.isChecked = false
            settingNotification0Check.isChecked =
                sharedPreferences.getBoolean(notiSettingArray[0], true)
            settingNotification1Check.isChecked =
                sharedPreferences.getBoolean(notiSettingArray[1], true)
            settingNotification2Check.isChecked =
                sharedPreferences.getBoolean(notiSettingArray[2], true)
            settingNotification3Check.isChecked =
                sharedPreferences.getBoolean(notiSettingArray[3], true)
            settingNotification4Check.isChecked =
                sharedPreferences.getBoolean(notiSettingArray[4], true)
        }
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

        sharedPreferences.edit {
            putBoolean(notiSettingArray[position], target.isChecked)
            apply()
        }
    }
}