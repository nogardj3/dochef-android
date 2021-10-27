package com.yhjoo.dochef.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger

object OtherUtil {
    fun log(vararg msgs: String) {
        Logger.d(msgs)
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
}