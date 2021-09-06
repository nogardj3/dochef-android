package com.yhjoo.dochef.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.yhjoo.dochef.R

object ChefAuth {
    private lateinit var mAuth: FirebaseAuth

    fun logOut(context: Context) {
        Utils.getSharedPreferences(context).edit {
            putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)
            remove(context.getString(R.string.SP_USERINFO))
            apply()
        }

        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
    }

    fun isLogIn(context: Context): Boolean {
        mAuth = FirebaseAuth.getInstance()
        val sharedPreferences = Utils.getSharedPreferences(context)

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