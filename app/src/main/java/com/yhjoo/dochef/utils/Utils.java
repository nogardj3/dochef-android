package com.yhjoo.dochef.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Pattern;

import static android.util.Patterns.EMAIL_ADDRESS;


public class Utils {
    public static void log(String... msgs) {
        String msg = TextUtils.join("\n", msgs);
        Log.d("YHJOO ", msg);
    }

    public enum EMAIL_VALIDATE {VALID, NODATA, INVALID}

    public enum PW_VALIDATE {VALID, NODATA, SHORT, LONG,INVALID}

    public enum NICKNAME_VALIDATE {VALID, NODATA, SHORT, LONG,INVALID}

    public static EMAIL_VALIDATE emailValidation(String email) {
        if (email.length() == 0)
            return EMAIL_VALIDATE.NODATA;
        else if (EMAIL_ADDRESS.matcher(email).matches())
            return EMAIL_VALIDATE.VALID;
        else
            return EMAIL_VALIDATE.INVALID;
    }


    public static PW_VALIDATE pwValidation(String pw) {
        String exp = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,16}$";

        if (pw.length() == 0)
            return PW_VALIDATE.NODATA;
        else if (pw.length() < 8)
            return PW_VALIDATE.SHORT;
        else if (pw.length() > 16)
            return PW_VALIDATE.LONG;
        else if (Pattern.compile(exp).matcher(pw).matches())
            return PW_VALIDATE.VALID;
        else
            return PW_VALIDATE.INVALID;
    }


    public static NICKNAME_VALIDATE nicknameValidate(String nickname) {
        String exp = "[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*";

        if (nickname.length() == 0)
            return NICKNAME_VALIDATE.NODATA;
        else if (nickname.length() < 6)
            return NICKNAME_VALIDATE.SHORT;
        else if (nickname.length() > 10)
            return NICKNAME_VALIDATE.LONG;
        else if (Pattern.compile(exp).matcher(nickname).matches())
            return NICKNAME_VALIDATE.VALID;
        else
            return NICKNAME_VALIDATE.INVALID;
    }
}