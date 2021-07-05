package com.yhjoo.dochef.utils;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Utils {
    public static void log(String... msgs) {
        String msg = TextUtils.join("\n", msgs);
        Log.d("YHJOO ", msg);
    }

    public static void showSnackbar(View rootview, String msg) {
        Snackbar.make(rootview, msg, Snackbar.LENGTH_SHORT).show();
    }
}
