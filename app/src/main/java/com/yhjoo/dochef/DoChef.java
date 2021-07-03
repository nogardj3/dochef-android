package com.yhjoo.dochef;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

public class DoChef extends Application {
    private static DoChef appInstance;
    private Toast toast;

    public static DoChef getAppInstance() {
        return appInstance;
    }

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();

        appInstance = this;

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        toast = Toast.makeText(this, "Default", Toast.LENGTH_SHORT);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                                               @Override
                                               public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                                                   activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                               }

                                               @Override
                                               public void onActivityStarted(Activity activity) {
                                               }

                                               @Override
                                               public void onActivityResumed(Activity activity) {
                                               }

                                               @Override
                                               public void onActivityPaused(Activity activity) {
                                               }

                                               @Override
                                               public void onActivityStopped(Activity activity) {
                                               }

                                               @Override
                                               public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                                               }

                                               @Override
                                               public void onActivityDestroyed(Activity activity) {
                                               }
                                           }
        );
    }

    public void showToast(String text) {
        toast.setText(text);
        toast.show();
    }
}
