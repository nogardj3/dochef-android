package com.yhjoo.dochef.utils

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.yhjoo.dochef.R

object AuthUtil {
    private lateinit var mAuth: FirebaseAuth

    fun logOut(context: Context) {
        DatastoreUtil.getSharedPreferences(context).edit {
            putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)
            remove(context.getString(R.string.SP_USERINFO))
            apply()
        }

        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
    }

    fun isLogIn(context: Context): Boolean {
        mAuth = FirebaseAuth.getInstance()
        val sharedPreferences = DatastoreUtil.getSharedPreferences(context)

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
                sharedPreferences.edit {
                    putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)
                    apply()
                }
            } else if (mAuth.currentUser != null) {
                mAuth.signOut()
            }

            false
        }
    }
}