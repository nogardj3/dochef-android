package com.yhjoo.dochef.utils;

import android.text.TextUtils;
import android.util.Log;


public class Utils {
    public static void log(String... msgs) {
        String msg = TextUtils.join("\n", msgs);
        Log.d("YHJOO ", msg);
    }
}