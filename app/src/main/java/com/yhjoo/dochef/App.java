package com.yhjoo.dochef;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.SocketException;

import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;


public class App extends Application {
    private static App appInstance;
    private static boolean isServerAlive;
    private Toast toast;

    public static App getAppInstance() {
        return appInstance;
    }

    public static boolean isServerAlive() {
        return isServerAlive;
    }

    public static void setIsServerAlive(boolean isServerAlive) {
        App.isServerAlive = isServerAlive;
    }

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                                          @Override
                                          public void accept(Throwable throwable) throws Throwable {

                                          }
                                      }

        );


        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if ((e instanceof IOException) || (e instanceof SocketException)) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application Thread.currentThread().getUncaughtExceptionHandler() .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) { // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                return;
            }
            Log.e("RxJava_HOOK", "Undeliverable exception received, not sure what to do" + e.getMessage());
        });

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
