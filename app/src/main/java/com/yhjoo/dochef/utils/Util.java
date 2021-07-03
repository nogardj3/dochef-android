package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.yhjoo.dochef.Preferences;

public class Util {
    public static void log(String... msgs) {
        String msg = TextUtils.join("\n",msgs);
        Log.d("YHJOO ", msg);
    }
}
