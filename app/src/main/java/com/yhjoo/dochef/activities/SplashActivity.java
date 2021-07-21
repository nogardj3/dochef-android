package com.yhjoo.dochef.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.ASplashBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class SplashActivity extends BaseActivity {
    ASplashBinding binding;
    FirebaseAnalytics mFirebaseAnalytics;

    boolean serverAlive = false;
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ASplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        checkServerAlive();
        checkIsAutoLogin();
        createChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_start));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_start));
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        ViewAnimator.animate(binding.splashLogo)
                .alpha(0.0f, 1.0f)
                .accelerate()
                .duration(500)
                .thenAnimate(binding.splashLogo)
                .alpha(1.0f, 1.0f)
                .duration(300)
                .onStop(this::goWhere)
                .start();
    }

    void goWhere() {
        Utils.log(serverAlive + "", isLogin + "");
        // 정상상태
        if (serverAlive && isLogin)
            startMain();
            // 로그인 풀려있음
        else if (!isLogin)
            startAccount();
            // 서버 죽음
        else {
            App.getAppInstance().showToast("서버가 동작하지 않습니다. 체험모드로 실행합니다.");
            startMain();
        }

        finish();
    }

    void checkServerAlive() {
        RxRetrofitServices.BasicService basicService =
                RxRetrofitBuilder.create(this, RxRetrofitServices.BasicService.class);

        compositeDisposable.add(
                basicService.checkAlive()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            App.setIsServerAlive(true);
                            serverAlive = true;
                        }, throwable -> {
                            throwable.printStackTrace();
                            App.setIsServerAlive(false);
                            serverAlive = false;
                        })
        );
    }

    void checkIsAutoLogin() {
        isLogin = ChefAuth.isLogIn(this);
    }

    void startAccount() {
        startActivity(new Intent(SplashActivity.this, AccountActivity.class));
    }

    void startMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    void createChannel(){
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean channel_created = mSharedPreferences.getBoolean("channel_created",false);
        Utils.log(channel_created);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !channel_created) {
            String CHANNEL_ID = getString(R.string.notification_channel_id);
            String name = "기본채널";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);

            FirebaseMessaging.getInstance().subscribeToTopic("admin")
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            task.getException().printStackTrace();
                            Utils.log(task.getException().toString());
                        }
                    });

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("channel_created", true);
            editor.apply();
        }
    }
}
