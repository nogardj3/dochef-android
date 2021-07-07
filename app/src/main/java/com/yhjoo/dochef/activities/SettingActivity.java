package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.BuildConfig;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    /*
        TODO
        1. percentlayout 지우기
        2. 로그아웃 기능 구현
        3. FCM 완료 후 알림 옵션 설정
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_setting);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.base_toolbar);
        toolbar.setTitle("도움말");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatTextView) findViewById(R.id.setting_version_text)).setText(BuildConfig.VERSION_NAME);
    }

    @OnClick({R.id.setting_notice, R.id.setting_faq, R.id.setting_tos, R.id.setting_notification_all_text, R.id.setting_notification_1_text,  R.id.setting_logout, R.id.setting_review})
    void oc(View v) {
        switch (v.getId()) {
            case R.id.setting_notice:
                startActivity(new Intent(SettingActivity.this, NoticeActivity.class));
                break;
            case R.id.setting_faq:
                startActivity(new Intent(SettingActivity.this, FAQActivity.class));
                break;
            case R.id.setting_tos:
                startActivity(new Intent(SettingActivity.this, TOSActivity.class));
                break;
            case R.id.setting_notification_all_text:
                ((AppCompatCheckBox) findViewById(R.id.setting_notification_all_check)).toggle();
                break;
            case R.id.setting_notification_1_text:
                ((AppCompatCheckBox) findViewById(R.id.setting_notification_1_check)).toggle();
                break;
            case R.id.setting_review:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        "https://play.google.com/store/apps/details?id=quvesoft.sprout"));
                intent.setPackage("com.android.vending");
                try{
                    startActivity(intent);
                }
                catch (Exception e){
                    Utils.log(e.toString());
                    App.getAppInstance().showToast("스토어 열기 실패");
                }
                break;
            case R.id.setting_logout:
                App.getAppInstance().showToast("로그아웃");
                ChefAuth.LogOut(this);
                finish();
                break;
        }
    }
}
