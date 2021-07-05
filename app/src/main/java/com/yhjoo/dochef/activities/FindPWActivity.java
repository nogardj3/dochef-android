package com.yhjoo.dochef.activities;

import android.os.Bundle;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class FindPWActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_findpw);
    }

    @OnClick(R.id.findpw_ok)
    void oc() {
        progressON(this);
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    progressOFF();
                    finish();
                });
    }
}
