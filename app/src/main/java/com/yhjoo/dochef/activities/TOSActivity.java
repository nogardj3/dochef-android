package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;

public class TOSActivity extends BaseActivity {
    /*
        TODO
        1. retrofit 구현
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_tos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tos_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (App.isServerAlive()) {

        } else
            ((AppCompatTextView) findViewById(R.id.tos_text)).setText(
                    Html.fromHtml(getString(R.string.tos_text),Html.FROM_HTML_MODE_LEGACY));
    }
}
