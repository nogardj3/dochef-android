package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.UserBrief;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static android.util.Patterns.EMAIL_ADDRESS;


public class Utils {
    public static boolean checkPermission(Context context, String[] RequirePermissions) {
        for (String a : RequirePermissions)
            if (ContextCompat.checkSelfPermission(context, a) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    public static void log(Object... msgs) {
        Logger.d("YHJOO %s", msgs);
    }

    public static UserBrief getUserBrief(Context context) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        UserBrief userInfo = gson.fromJson(mSharedPreferences.getString(context.getString(R.string.SP_USERINFO), null), UserBrief.class);

        return userInfo;
    }

    public enum EMAIL_VALIDATE {VALID, NODATA, INVALID}

    public enum PW_VALIDATE {VALID, NODATA, SHORT, LONG, INVALID}

    public enum NICKNAME_VALIDATE {VALID, NODATA, SHORT, LONG, INVALID}

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

    public static String convertMillisToText(long millis) {
        long current_millis = new Date().getTime();
        long diff_sec = (current_millis - millis) / 1000;

        if (diff_sec < 60)
            return "방금 전";
        else if (diff_sec / 60 < 60)
            return (diff_sec / 60) + "분 전";
        else if (diff_sec / 60 / 60 < 24)
            return (diff_sec / 60 / 60) + "시간 전";
        else if (diff_sec / 60 / 60 / 24 < 7)
            return (diff_sec / 60 / 60 / 24) + "일 전";
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(new Date(millis));
        }
    }

    public static boolean checkNew(long millis) {
        long current_millis = new Date().getTime();
        long diff_sec = (current_millis - millis) / 1000;

        return diff_sec / 60 / 60 / 24 < 3;
    }
}