package com.yhjoo.dochef.utils

import android.util.Patterns
import androidx.core.util.PatternsCompat
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

    fun emailValidate(email: String): Pair<Int, String?> {
        return when {
            email.isEmpty() ->
                Pair(
                    EmailResult.ERR_EMPTY,
                    "이메일을 입력 해 주세요."
                )
            !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches() ->
                Pair(
                    EmailResult.ERR_INVALID,
                    "이메일 형식이 올바르지 않습니다."
                )
            else ->
                Pair(
                    EmailResult.VALID,
                    null
                )
        }
    }

    fun pwValidate(pw: String): Pair<Int, String?> {
        val regex = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,16}$"

        return when {
            pw.isEmpty() ->
                Pair(
                    PwResult.ERR_EMPTY,
                    "비밀번호를 입력 해 주세요."
                )
            pw.length < 8 || pw.length > 16 ->
                Pair(
                    PwResult.ERR_LENGTH,
                    "비밀번호 길이를 확인 해 주세요. 8자 이상, 16자 이하로 입력 해 주세요."
                )
            !Pattern.compile(regex).matcher(pw).matches() ->
                Pair(
                    PwResult.ERR_INVALID,
                    "비밀번호 형식을 확인 해 주세요. 영문 및 숫자를 포함해야 합니다."
                )
            else ->
                Pair(
                    PwResult.VALID,
                    null
                )
        }
    }

    fun nicknameValidate(nickname: String): Pair<Int, String?> {
        val regex = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*"

        return when {
            nickname.isEmpty() ->
                Pair(
                    NicknameResult.ERR_EMPTY,
                    "비밀번호를 입력 해 주세요."
                )
            nickname.length < 6 || nickname.length > 10 ->
                Pair(
                    NicknameResult.ERR_LENGTH,
                    "닉네임 길이를 확인 해 주세요. 6자 이상, 10자 이하로 입력해주세요."
                )
            !Pattern.compile(regex).matcher(nickname).matches() ->
                Pair(
                    NicknameResult.ERR_INVALID,
                    "닉네임 형식을 확인 해 주세요. 숫자, 알파벳 대소문자, 한글만 사용가능합니다."
                )
            else ->
                Pair(
                    NicknameResult.VALID,
                    null
                )
        }
    }
}