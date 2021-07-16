package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.adapter.SearchViewPagerAdapter;
import com.yhjoo.dochef.databinding.ASearchBinding;
import com.yhjoo.dochef.fragments.ResultFragment;
import com.yhjoo.dochef.model.SearchType;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    ASearchBinding binding;

    SearchViewPagerAdapter viewPagerAdapter;

    List<SearchType> Types = new ArrayList<>();
    String keyword;

    /*
        TODO
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ASearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this);

        viewPagerAdapter = new SearchViewPagerAdapter(getSupportFragmentManager());
        Types.add(new SearchType(1, "레시피"));
        Types.add(new SearchType(2, "유저"));
        Types.add(new SearchType(3, "재료"));
        Types.add(new SearchType(4, "태그"));
        for (int i = 0; i < Types.size(); i++) {
            viewPagerAdapter.addFragment(Types.get(i).getFragment(), Types.get(i).getTitle());
        }

        binding.searchViewpager.setAdapter(viewPagerAdapter);
        binding.searchViewpager.setOffscreenPageLimit(4);

        binding.searchEdittext.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = binding.searchEdittext.getText().toString();
                ((ResultFragment) viewPagerAdapter.getItem(binding.searchViewpager.getCurrentItem())).search();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.searchEdittext.getWindowToken(), 0);
            }
            return true;
        });

        binding.searchTablayout.setupWithViewPager(binding.searchViewpager);
        binding.searchBtnSearch.setOnClickListener(this::onClickSearch);
        binding.searchBtnBack.setOnClickListener(this::onClickBack);
    }

    public String getKeyword() {
        return keyword;
    }

    void onClickSearch(View v){
        if (binding.searchEdittext.getText().toString().trim().length() == 0) {
            App.getAppInstance().showToast("한 글자 이상 입력해주세요.");
        } else {
            this.keyword = binding.searchEdittext.getText().toString().trim();
            binding.searchEdittext.setText(this.keyword);

            switch (binding.searchViewpager.getCurrentItem()) {
                case 0:
                    ((ResultFragment) viewPagerAdapter.getItem(0)).search();
                    break;
                case 1:
                    ((ResultFragment) viewPagerAdapter.getItem(1)).search();
                    break;
                case 2:
                    ((ResultFragment) viewPagerAdapter.getItem(2)).search();
                    break;
                case 3:
                    ((ResultFragment) viewPagerAdapter.getItem(3)).search();
                    break;

            }
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.searchEdittext.getWindowToken(), 0);
        }
    }

    void onClickBack(View v){
        finish();
    }
}
