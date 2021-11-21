package com.yhjoo.dochef.utils

import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ValidateUtilTest {
    @Test
    fun emailValidate() {
        assertSame(
            ValidateUtil.EmailResult.ERR_EMPTY,
            ValidateUtil.emailValidate("").first
        )
        assertSame(
            ValidateUtil.EmailResult.ERR_INVALID,
            ValidateUtil.emailValidate("aaa").first
        )
        assertSame(
            ValidateUtil.EmailResult.VALID,
            ValidateUtil.emailValidate("aaa@bb.com").first
        )
    }

    @Test
    fun pwValidate() {
        assertSame(
            ValidateUtil.PwResult.VALID,
            ValidateUtil.pwValidate("1234aaaaa").first
        )
        assertSame(
            ValidateUtil.PwResult.ERR_LENGTH,
            ValidateUtil.pwValidate("aaaaaaaaaaaaaaaaaa").first
        )
        assertSame(
            ValidateUtil.PwResult.ERR_EMPTY,
            ValidateUtil.pwValidate("").first
        )
        assertSame(
            ValidateUtil.PwResult.ERR_INVALID,
            ValidateUtil.pwValidate("33333333").first
        )
    }

    @Test
    fun nicknameValidate() {
        assertSame(
            ValidateUtil.NicknameResult.VALID,
            ValidateUtil.nicknameValidate("hello_world").first
        )
        assertSame(
            ValidateUtil.NicknameResult.ERR_EMPTY,
            ValidateUtil.nicknameValidate("").first
        )
        assertSame(
            ValidateUtil.NicknameResult.ERR_INVALID,
            ValidateUtil.nicknameValidate("jeifjiejifjisjasfasffipjpiz").first
        )
    }
}