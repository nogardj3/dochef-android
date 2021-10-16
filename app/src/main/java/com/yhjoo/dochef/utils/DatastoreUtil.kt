package com.yhjoo.dochef.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.UserBrief

object DatastoreUtil {
    fun getUserBrief(context: Context): UserBrief {
        val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            context.applicationContext
        )

        val gson = Gson()
        return if (App.isServerAlive) gson.fromJson(
            mSharedPreferences.getString(context.getString(R.string.SP_USERINFO), null),
            UserBrief::class.java
        )
        else {
            DataGenerator.make(
                context.resources,
                context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
            )
        }
    }

    fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }
}