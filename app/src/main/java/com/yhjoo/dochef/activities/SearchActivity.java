package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.ads.MobileAds;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.databinding.ASearchBinding;
import com.yhjoo.dochef.fragments.ResultFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {
    ASearchBinding binding;
    SearchViewPagerAdapter viewPagerAdapter;

    List<SearchType> Types = new ArrayList<>();
    String keyword;

    /*
        TODO
        1. search type 개선
        2. 홈버튼 뭐지
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
            viewPagerAdapter.addFragment(Types.get(i).fragment, Types.get(i).title);
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

    class SearchType {
        ResultFragment fragment;
        String title;

        SearchType(int type, String title) {
            this.title = title;
            this.fragment = new ResultFragment();
            Bundle b = new Bundle();
            b.putInt("type", type);
            fragment.setArguments(b);
        }
    }

    class SearchViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        SearchViewPagerAdapter(FragmentManager Fm) {
            super(Fm);
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
