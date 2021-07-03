package quvesoft.project2.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtil {
    public static boolean checkPermission(Context context, String[] RequirePermissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (String a : RequirePermissions)
                if (ContextCompat.checkSelfPermission(context, a) != PackageManager.PERMISSION_GRANTED)
                    return false;

            return true;
        } else
            return true;
    }
}
