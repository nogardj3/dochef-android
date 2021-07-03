package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;

import com.yhjoo.dochef.Preferences;

public class ChefAuth {
    public static void LogOut(Context context) {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false);
        editor.remove(Preferences.SHAREDPREFERENCE_USERINFO);
        editor.apply();
    }

    public static boolean isLogIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false)
                && FirebaseAuth.getInstance().getCurrentUser() != null) {
            return true;
        } else {
            if (sharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false);
                editor.apply();
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
            }
            return false;
        }
    }
}
