package com.yhjoo.dochef.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionUtil {
    public static boolean checkPermission(Context context, String[] RequirePermissions) {
        for (String a : RequirePermissions)
            if (ContextCompat.checkSelfPermission(context, a) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }
}
