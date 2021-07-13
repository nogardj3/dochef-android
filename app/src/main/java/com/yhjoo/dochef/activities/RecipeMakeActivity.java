package com.yhjoo.dochef.activities;

import android.os.Bundle;

import com.yhjoo.dochef.databinding.ARecipemakeBinding;

public class RecipeMakeActivity extends BaseActivity {
    enum MODE {WRITE, REVISE}

    ARecipemakeBinding binding;

    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
        3. 기능 구현
        4. revise 기능 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ARecipemakeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.recipemakeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}