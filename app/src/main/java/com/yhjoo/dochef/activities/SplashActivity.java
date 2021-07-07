package com.yhjoo.dochef.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.Utils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.splash_logo)
    AppCompatImageView logo;
    @BindView(R.id.splash_logo2)
    AppCompatImageView logo2;

    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean serverAlive = false;
    private boolean isLogin = false;
    /*
        TODO
        1. 서버 상태 확인하기
        2. 로그인 여부 확인
        3. 애니메이션 다시
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_splash);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        checkServerAlive();
        checkIsAutoLogin();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_start));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_start));
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        ViewAnimator.animate(logo)
                .alpha(0.0f, 1.0f)
                .duration(200)
                .andAnimate(logo)
                .translationY(-1000, 0)
                .duration(200)
                .decelerate()
                .thenAnimate(logo)
                .swing()
                .duration(200)
                .thenAnimate(logo2)
                .alpha(0.0f, 1.0f)
                .accelerate()
                .duration(200)
                .thenAnimate(logo2)
                .duration(200)
                .onStop(this::goWhere)
                .start();
    }

    private void goWhere(){
        Utils.log("------------------------------------",Boolean.toString(serverAlive) , Boolean.toString(isLogin));
        // 정상상태
        if(serverAlive && isLogin)
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

    private void checkServerAlive(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitServices.BasicService basicService= retrofit.create(RetrofitServices.BasicService.class);

        basicService.getTOS().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> res) {
                App.setIsServerAlive(true);
                serverAlive = true;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                App.setIsServerAlive(false);
                serverAlive = false;
            }
        });
    }

    private void checkIsAutoLogin(){
        isLogin = ChefAuth.isLogIn(this);
    }

    private void startAccount(){
        startActivity(new Intent(SplashActivity.this, AccountActivity.class));
    }

    private void startMain(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
}
