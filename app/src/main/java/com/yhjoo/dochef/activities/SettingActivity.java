package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.BuildConfig;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.ASettingBinding;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.Utils;

public class SettingActivity extends BaseActivity {
    ASettingBinding binding;
    SharedPreferences mSharedPreferences;
    String[] sp_array;

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

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp_array = getResources().getStringArray(R.array.sp_noti);

        binding.settingNotice.setOnClickListener(this::startNotice);
        binding.settingVersion.setText(BuildConfig.VERSION_NAME);
        binding.settingFaq.setOnClickListener(this::startFAQ);
        binding.settingTos.setOnClickListener(this::startTOS);
        binding.settingLogout.setOnClickListener(this::signOut);

        binding.settingNotificationAllCheck.setOnClickListener((v -> toggleAllnotification()));
        binding.settingNotification0Check.setOnClickListener((v -> toggleNotification(0,null)));
        binding.settingNotification1Check.setOnClickListener((v -> toggleNotification(1,null)));
        binding.settingNotification2Check.setOnClickListener((v -> toggleNotification(2,null)));
        binding.settingNotification3Check.setOnClickListener((v -> toggleNotification(3,null)));
        binding.settingNotification4Check.setOnClickListener((v -> toggleNotification(4,null)));

        getNotiSettings();
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

    void getNotiSettings(){
        binding.settingNotificationAllCheck.setChecked(false);
        binding.settingNotification0Check.setChecked(mSharedPreferences.getBoolean(sp_array[0],true));
        binding.settingNotification1Check.setChecked(mSharedPreferences.getBoolean(sp_array[1],true));
        binding.settingNotification2Check.setChecked(mSharedPreferences.getBoolean(sp_array[2],true));
        binding.settingNotification3Check.setChecked(mSharedPreferences.getBoolean(sp_array[3],true));
        binding.settingNotification4Check.setChecked(mSharedPreferences.getBoolean(sp_array[4],true));
    }

    void toggleAllnotification() {
        toggleNotification(0,binding.settingNotificationAllCheck.isChecked());
        toggleNotification(1,binding.settingNotificationAllCheck.isChecked());
        toggleNotification(2,binding.settingNotificationAllCheck.isChecked());
        toggleNotification(3,binding.settingNotificationAllCheck.isChecked());
        toggleNotification(4,binding.settingNotificationAllCheck.isChecked());
    }

    void toggleNotification(int position,@Nullable Boolean check) {
        AppCompatCheckBox target;
        switch(position){
            case 0:
                target = binding.settingNotification0Check;
                break;
            case 1:
                target = binding.settingNotification1Check;
                break;
            case 2:
                target = binding.settingNotification2Check;
                break;
            case 3:
                target = binding.settingNotification3Check;
                break;
            case 4:
                target = binding.settingNotification4Check;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }

        if(check!=null){
            target.setChecked(check);
        }
        Utils.log(target.isChecked());

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(sp_array[position],target.isChecked());
        editor.apply();
    }
}
