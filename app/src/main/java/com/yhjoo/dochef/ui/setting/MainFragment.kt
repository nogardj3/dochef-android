package com.yhjoo.dochef.ui.setting

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yhjoo.dochef.App
import com.yhjoo.dochef.BuildConfig
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.BasicRepository
import com.yhjoo.dochef.databinding.SettingFragmentBinding
import com.yhjoo.dochef.ui.account.AccountActivity
import com.yhjoo.dochef.utils.AuthUtil
import com.yhjoo.dochef.utils.DatastoreUtil

class MainFragment : Fragment() {
    /* TODO
    1. SharedPreferences -> Datastore
     */

    private lateinit var binding: SettingFragmentBinding
    private val settingViewModel: SettingViewModel by activityViewModels {
        SettingViewModelFactory(
            BasicRepository(requireContext().applicationContext)
        )
    }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notiSettingArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_fragment, container, false)
        val view: View = binding.root

        sharedPreferences = DatastoreUtil.getSharedPreferences(requireContext())
        notiSettingArray = resources.getStringArray(R.array.sp_noti)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

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

        return view
    }

    private fun startNotice() {
        findNavController().navigate(R.id.action_settingMainFragment_to_settingNoticeFragment)
    }

    private fun startFAQ() {
        findNavController().navigate(R.id.action_settingMainFragment_to_settingFAQFragment)
    }

    private fun startTOS() {
        findNavController().navigate(R.id.action_settingMainFragment_to_settingTosFragment)
    }

    private fun signOut() {
        App.showToast("로그아웃")
        AuthUtil.logOut(requireContext())

        startActivity(
            Intent(requireContext(), AccountActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        requireActivity().finish()
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