package com.yhjoo.dochef.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import androidx.appcompat.widget.AppCompatCheckBox
import com.yhjoo.dochef.App.Companion.appInstance
import com.yhjoo.dochef.BuildConfig
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.ASettingBinding
import com.yhjoo.dochef.utils.*

class SettingActivity : BaseActivity() {
    var binding: ASettingBinding? = null
    var mSharedPreferences: SharedPreferences? = null
    var sp_array: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ASettingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sp_array = resources.getStringArray(R.array.sp_noti)
        binding!!.settingNotice.setOnClickListener { view: View? -> startNotice(view) }
        binding!!.settingVersion.text = BuildConfig.VERSION_NAME
        binding!!.settingFaq.setOnClickListener { view: View? -> startFAQ(view) }
        binding!!.settingTos.setOnClickListener { view: View? -> startTOS(view) }
        binding!!.settingLogout.setOnClickListener { view: View? -> signOut(view) }
        binding!!.settingNotificationAllCheck.setOnClickListener { v: View? -> toggleAllnotification() }
        binding!!.settingNotification0Check.setOnClickListener { v: View? ->
            toggleNotification(
                0,
                null
            )
        }
        binding!!.settingNotification1Check.setOnClickListener { v: View? ->
            toggleNotification(
                1,
                null
            )
        }
        binding!!.settingNotification2Check.setOnClickListener { v: View? ->
            toggleNotification(
                2,
                null
            )
        }
        binding!!.settingNotification3Check.setOnClickListener { v: View? ->
            toggleNotification(
                3,
                null
            )
        }
        binding!!.settingNotification4Check.setOnClickListener { v: View? ->
            toggleNotification(
                4,
                null
            )
        }
        notiSettings
    }

    fun startNotice(view: View?) {
        startActivity(Intent(this@SettingActivity, NoticeActivity::class.java))
    }

    fun startFAQ(view: View?) {
        startActivity(Intent(this@SettingActivity, FAQActivity::class.java))
    }

    fun startTOS(view: View?) {
        startActivity(Intent(this@SettingActivity, TOSActivity::class.java))
    }

    fun signOut(view: View?) {
        appInstance!!.showToast("로그아웃")
        ChefAuth.logOut(this)
        val intent = Intent(this, AccountActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    val notiSettings: Unit
        get() {
            binding!!.settingNotificationAllCheck.isChecked = false
            binding!!.settingNotification0Check.isChecked =
                mSharedPreferences!!.getBoolean(sp_array[0], true)
            binding!!.settingNotification1Check.isChecked =
                mSharedPreferences!!.getBoolean(sp_array[1], true)
            binding!!.settingNotification2Check.isChecked =
                mSharedPreferences!!.getBoolean(sp_array[2], true)
            binding!!.settingNotification3Check.isChecked =
                mSharedPreferences!!.getBoolean(sp_array[3], true)
            binding!!.settingNotification4Check.isChecked =
                mSharedPreferences!!.getBoolean(sp_array[4], true)
        }

    fun toggleAllnotification() {
        toggleNotification(0, binding!!.settingNotificationAllCheck.isChecked)
        toggleNotification(1, binding!!.settingNotificationAllCheck.isChecked)
        toggleNotification(2, binding!!.settingNotificationAllCheck.isChecked)
        toggleNotification(3, binding!!.settingNotificationAllCheck.isChecked)
        toggleNotification(4, binding!!.settingNotificationAllCheck.isChecked)
    }

    fun toggleNotification(position: Int, check: Boolean?) {
        val target: AppCompatCheckBox
        target = when (position) {
            0 -> binding!!.settingNotification0Check
            1 -> binding!!.settingNotification1Check
            2 -> binding!!.settingNotification2Check
            3 -> binding!!.settingNotification3Check
            4 -> binding!!.settingNotification4Check
            else -> throw IllegalStateException("Unexpected value: $position")
        }
        if (check != null) {
            target.isChecked = check
        }
        Utils.log(target.isChecked)
        val editor = mSharedPreferences!!.edit()
        editor.putBoolean(sp_array[position], target.isChecked)
        editor.apply()
    }
}