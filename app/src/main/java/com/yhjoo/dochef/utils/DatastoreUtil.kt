package com.yhjoo.dochef.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.UserBrief

object DatastoreUtil {
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

    fun getSharedPreferences(context: Context): SharedPreferences {
//        val dataStore = context. createDataStore(name = "settings_pref")

        return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }
}