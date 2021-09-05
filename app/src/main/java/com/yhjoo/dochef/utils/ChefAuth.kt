package com.yhjoo.dochef.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.yhjoo.dochef.R

object ChefAuth {
    private lateinit var mAuth: FirebaseAuth

    fun logOut(context: Context) {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor = sharedPreferences.edit()

        editor.putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)
        editor.remove(context.getString(R.string.SP_USERINFO))
        editor.apply()

        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
    }

    fun isLogIn(context: Context): Boolean {
        mAuth = FirebaseAuth.getInstance()
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return if (sharedPreferences.getBoolean(
                context.getString(R.string.SP_ACTIVATEDDEVICE),
                false
            )
            && mAuth.currentUser != null
        ) {
            true
        } else {
            if (sharedPreferences.getBoolean(
                    context.getString(R.string.SP_ACTIVATEDDEVICE),
                    false
                )
            ) {
                val editor = sharedPreferences.edit()
                editor.putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)
                editor.apply()
            } else if (mAuth.currentUser != null) {
                mAuth.signOut()
            }

            false
        }
    }
}