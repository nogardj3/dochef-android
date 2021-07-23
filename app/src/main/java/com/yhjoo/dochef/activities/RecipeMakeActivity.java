package com.yhjoo.dochef.activities;

import android.os.Bundle;

import com.yhjoo.dochef.databinding.ARecipemakeBinding;

public class RecipeMakeActivity extends BaseActivity {
    enum MODE {WRITE, REVISE}

    ARecipemakeBinding binding;

    /*
        TODO
        모든 기능
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