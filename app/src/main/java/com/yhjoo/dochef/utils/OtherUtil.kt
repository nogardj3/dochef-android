package com.yhjoo.dochef.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

object OtherUtil {
    fun log(vararg msgs: String) {
        Logger.d("YHJOO %s", msgs.joinToString())
    }

    fun millisToText(millis: Long): String {
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