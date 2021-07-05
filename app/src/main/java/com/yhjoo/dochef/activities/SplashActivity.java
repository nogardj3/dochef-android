package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.percentlayout.widget.PercentRelativeLayout;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.splash_logo)
    AppCompatImageView logo;
    @BindView(R.id.splash_logo2)
    AppCompatImageView logo2;
    @BindView(R.id.splash_root)
    PercentRelativeLayout root;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_splash);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        Observable.timer(2, TimeUnit.SECONDS)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(count -> checkInternet());
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_start));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_start));
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        Utils.showSnackbar(root,"hello~!@");

        ViewAnimator.animate(logo)
                .alpha(0.0f, 1.0f)
                .duration(500)
                .andAnimate(logo)
                .translationY(-1000, 0)
                .duration(800)
                .decelerate()
                .thenAnimate(logo)
                .swing()
                .duration(600)
                .thenAnimate(logo2)
                .alpha(0.0f, 1.0f)
                .accelerate()
                .duration(400)
                .thenAnimate(logo2)
                .duration(200)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        checkInternet();
                    }
                })
                .start();
    }

    private void checkInternet() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "네트워크 연결을 확인 해 주세요.", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .setAction("재시도", v -> checkInternet()).show();
        }
    }
}
