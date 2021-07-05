package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.yhjoo.dochef.Preferences;

public class ChefAuth {
    private static FirebaseAuth mAuth;

    public static void LogOut(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false);
        editor.remove(Preferences.SHAREDPREFERENCE_USERINFO);
        editor.apply();
    }

    public static boolean isLogIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false)
                && mAuth.getCurrentUser() != null) {
            return true;
        } else {
            if (sharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false);
                editor.apply();
            } else if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }
            return false;
        }
    }
}
