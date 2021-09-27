package com.yhjoo.dochef.utils

import android.util.Patterns
import java.util.*
import java.util.regex.Pattern

object ValidateUtil {
    object EmailResult {
        const val VALID = 0
        const val ERR_EMPTY = 1
        const val ERR_INVALID = 2
    }

    object PwResult {
        const val VALID = 0
        const val ERR_EMPTY = 1
        const val ERR_LENGTH = 2
        const val ERR_INVALID = 3
    }

    object NicknameResult {
        const val VALID = 0
        const val ERR_EMPTY = 1
        const val ERR_LENGTH = 2
        const val ERR_INVALID = 3
    }

    fun emailValidate(email: String): Int {
        return when {
            email.isEmpty() -> EmailResult.ERR_EMPTY
            Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailResult.VALID
            else -> EmailResult.ERR_INVALID
        }
    }

    fun pwValidate(pw: String): Int {
        val regex = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,16}$"

        return when {
            pw.isEmpty() -> PwResult.ERR_EMPTY
            pw.length < 8 || pw.length > 16 -> PwResult.ERR_LENGTH
            Pattern.compile(regex).matcher(pw).matches() -> PwResult.VALID
            else -> PwResult.ERR_INVALID
        }
    }

    fun nicknameValidate(nickname: String): Int {
        val regex = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*"

        return when {
            nickname.isEmpty() -> NicknameResult.ERR_EMPTY
            nickname.length < 6 || nickname.length > 10 -> NicknameResult.ERR_LENGTH
            Pattern.compile(regex).matcher(nickname).matches() -> NicknameResult.VALID
            else -> NicknameResult.ERR_INVALID
        }
    }

    fun checkNew(millis: Long): Boolean {
        val currentMillis = Date().time
        val secDiff = (currentMillis - millis) / 1000

        return secDiff / 60 / 60 / 24 < 3
    }
}