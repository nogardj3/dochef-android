package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.utils.ChefAuth;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_setting);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.setting_notice, R.id.setting_help, R.id.setting_tos, R.id.setting_noti_all_layout, R.id.setting_noti1_layout, R.id.setting_option1_layout, R.id.setting_logout})
    void oc(View v) {
        switch (v.getId()) {
            case R.id.setting_notice:
                startActivity(new Intent(SettingActivity.this, NoticeActivity.class));
                break;
            case R.id.setting_help:
                startActivity(new Intent(SettingActivity.this, HelpActivity.class));
                break;
            case R.id.setting_tos:
                startActivity(new Intent(SettingActivity.this, TOSActivity.class));
                break;
            case R.id.setting_noti_all_layout:
                ((AppCompatCheckBox) findViewById(R.id.setting_noti_all)).toggle();
                break;
            case R.id.setting_noti1_layout:
                ((AppCompatCheckBox) findViewById(R.id.setting_noti1)).toggle();
                break;
            case R.id.setting_option1_layout:
                ((AppCompatCheckBox) findViewById(R.id.setting_option1)).toggle();
                break;
            case R.id.setting_logout:
                App.getAppInstance().showToast("로그아웃");
                ChefAuth.LogOut(this);
                finish();
                break;
        }
    }
}
