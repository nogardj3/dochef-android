package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.BuildConfig;
import com.yhjoo.dochef.databinding.ASettingBinding;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.Utils;

public class SettingActivity extends BaseActivity {
    ASettingBinding binding;

    /*
        TODO
        FCM 완료 후 알림 옵션 설정
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ASettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.settingVersionText.setText(BuildConfig.VERSION_NAME);
        binding.settingNotice.setOnClickListener(this::startNotice);
        binding.settingFaq.setOnClickListener(this::startFAQ);
        binding.settingTos.setOnClickListener(this::startTOS);
        binding.settingNotificationAllText.setOnClickListener(this::toggleNotificationAll);
        binding.settingNotification1Text.setOnClickListener(this::toggleNotificationItem);
        binding.settingReview.setOnClickListener(this::goReview);
        binding.settingLogout.setOnClickListener(this::signOut);
    }

    void startNotice(View view) {
        startActivity(new Intent(SettingActivity.this, NoticeActivity.class));
    }

    void startFAQ(View view) {
        startActivity(new Intent(SettingActivity.this, FAQActivity.class));
    }

    void startTOS(View view) {
        startActivity(new Intent(SettingActivity.this, TOSActivity.class));
    }

    void goReview(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=quvesoft.sprout"))
                .setPackage("com.android.vending");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Utils.log(e.toString());
            App.getAppInstance().showToast("스토어 열기 실패");
        }
    }

    void signOut(View view) {
        App.getAppInstance().showToast("로그아웃");
        ChefAuth.LogOut(this);

        Intent intent = new Intent(this, AccountActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    void toggleNotificationAll(View view) {
        binding.settingNotificationAllCheck.toggle();
    }

    void toggleNotificationItem(View view) {
        binding.settingNotification1Check.toggle();
    }
}
