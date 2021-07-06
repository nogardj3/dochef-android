package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.yhjoo.dochef.R;

public class ChefAuth {
    private static FirebaseAuth mAuth;

    public static void LogOut(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), false);
        editor.remove(context.getString(R.string.SHAREDPREFERENCE_USERINFO));
        editor.apply();
    }

    public static boolean isLogIn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean(context.getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), false)
                && mAuth.getCurrentUser() != null) {
            return true;
        } else {
            if (sharedPreferences.getBoolean(context.getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(context.getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), false);
                editor.apply();
            } else if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }
            return false;
        }
    }
}
