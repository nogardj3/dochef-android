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

        binding.settingNotice.setOnClickListener(this::startNotice);
        binding.settingNotificationAllText.setOnClickListener(this::toggleNotificationAll);
        binding.settingNotification1Text.setOnClickListener(this::toggleNotificationItem);
        binding.settingVersion.setText(BuildConfig.VERSION_NAME);
        binding.settingFaq.setOnClickListener(this::startFAQ);
        binding.settingTos.setOnClickListener(this::startTOS);
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
