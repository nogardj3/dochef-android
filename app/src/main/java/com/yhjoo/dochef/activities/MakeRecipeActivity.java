package com.yhjoo.dochef.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;

public class MakeRecipeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_makerecipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.makerecipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}