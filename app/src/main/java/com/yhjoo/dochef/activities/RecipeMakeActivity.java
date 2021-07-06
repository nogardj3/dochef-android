package com.yhjoo.dochef.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;

public class RecipeMakeActivity extends BaseActivity {

    /*
        TODO
        1. 기능 구현
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_recipemake);

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipemake_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}