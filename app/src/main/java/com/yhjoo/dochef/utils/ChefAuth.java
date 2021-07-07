package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.yhjoo.dochef.R;

public class ChefAuth {
    private static FirebaseAuth mAuth;

    public static void LogOut(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false);
        editor.remove(context.getString(R.string.SP_USERINFO));
        editor.apply();

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    public static boolean isLogIn(Context context) {
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)
                && mAuth.getCurrentUser() != null) {
            return true;
        } else {
            if (sharedPreferences.getBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(context.getString(R.string.SP_ACTIVATEDDEVICE), false);
                editor.apply();
            } else if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }
            return false;
        }
    }
}
