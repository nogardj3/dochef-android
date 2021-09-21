package com.yhjoo.dochef.utilities

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Patterns
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.yhjoo.dochef.R
import com.yhjoo.dochef.model.UserBrief
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Utils {
    // TODO
    // 1. TimeDiff 만들기

    object EmailValidate {
        const val VALID = 0
        const val NODATA = 1
        const val INVALID = 2
    }

    object PWValidate {
        const val VALID = 0
        const val NODATA = 1
        const val LENGTH = 2
        const val INVALID = 3
    }

    object NicknameValidate {
        const val VALID = 0
        const val NODATA = 1
        const val LENGTH = 2
        const val INVALID = 3
    }

    fun checkPermission(context: Context, RequirePermissions: Array<String>): Boolean {
        for (a in RequirePermissions)
            if (ContextCompat.checkSelfPermission(
                    context,
                    a
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        return true
    }

    fun log(vararg msgs: String) {
        Logger.d("YHJOO %s", msgs.joinToString())
    }

    fun getUserBrief(context: Context): UserBrief {
        val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context.applicationContext
        )

        val gson = Gson()
        return gson.fromJson(
            mSharedPreferences.getString(context.getString(R.string.SP_USERINFO), null),
            UserBrief::class.java
        )
    }

    fun emailValidation(email: String): Int {
        return when {
            email.isEmpty() -> EmailValidate.NODATA
            Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidate.VALID
            else -> EmailValidate.INVALID
        }
    }

    fun pwValidation(pw: String): Int {
        val regex = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,16}$"

        return when {
            pw.isEmpty() -> PWValidate.NODATA
            pw.length < 8 || pw.length > 16 -> PWValidate.LENGTH
            Pattern.compile(regex).matcher(pw).matches() -> PWValidate.VALID
            else -> PWValidate.INVALID
        }
    }

    fun nicknameValidate(nickname: String): Int {
        val regex = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*"

        return when {
            nickname.isEmpty() -> NicknameValidate.NODATA
            nickname.length < 6 || nickname.length > 10 -> NicknameValidate.LENGTH
            Pattern.compile(regex).matcher(nickname).matches() -> NicknameValidate.VALID
            else -> NicknameValidate.INVALID
        }
    }

    fun convertMillisToText(millis: Long): String {
        val currentMillis = Date().time
        val secDiff = (currentMillis - millis) / 1000

        return when {
            secDiff < 60 -> "방금 전"
            secDiff / 60 < 60 -> (secDiff / 60).toString() + "분 전"
            secDiff / 60 / 60 < 24 -> (secDiff / 60 / 60).toString() + "시간 전"
            secDiff / 60 / 60 / 24 < 7 -> (secDiff / 60 / 60 / 24).toString() + "일 전"
            else -> {
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                formatter.format(Date(millis))
            }
        }
    }

    fun checkNew(millis: Long): Boolean {
        val currentMillis = Date().time
        val secDiff = (currentMillis - millis) / 1000

        return secDiff / 60 / 60 / 24 < 3
    }

    fun getSharedPreferences(context: Context): SharedPreferences {

//        val dataStore = context. createDataStore(name = "settings_pref")

        return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }
}