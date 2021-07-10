package com.yhjoo.dochef.activities;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.fragments.ResultFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {
    private final List<SearchType> Types = new ArrayList<>();
    @BindView(R.id.search_viewpager)
    ViewPager viewPager;
    @BindView(R.id.search_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.search_btn_back)
    AppCompatImageView backButton;
    @BindView(R.id.search_btn_search)
    AppCompatImageView searchButton;
    @BindView(R.id.search_edittext)
    AppCompatEditText searchview;
    private SearchViewPagerAdapter viewPagerAdapter;
    private String keyword;

    /*
        TODO
        1. search type 개선
        2. 홈버튼 뭐지
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_search);
        ButterKnife.bind(this);
        MobileAds.initialize(this);

        viewPagerAdapter = new SearchViewPagerAdapter(getSupportFragmentManager());
        Types.add(new SearchType(1, "레시피"));
        Types.add(new SearchType(2, "유저"));
        Types.add(new SearchType(3, "재료"));
        Types.add(new SearchType(4, "태그"));

        for (int i = 0; i < Types.size(); i++) {
            viewPagerAdapter.addFragment(Types.get(i).fragment, Types.get(i).title);
        }


        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        searchview.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = searchview.getText().toString();
                ((ResultFragment) viewPagerAdapter.getItem(viewPager.getCurrentItem())).search();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchview.getWindowToken(), 0);
            }
            return true;
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    public String getKeyword() {
        return keyword;
    }

    @OnClick({R.id.search_btn_search, R.id.search_btn_back})
    public void click(View vv) {
        switch (vv.getId()) {
            case R.id.search_btn_search:
                if (searchview.getText().toString().trim().length() == 0) {
                    App.getAppInstance().showToast("한 글자 이상 입력해주세요.");
                } else {
                    this.keyword = searchview.getText().toString().trim();
                    searchview.setText(this.keyword);

                    switch (viewPager.getCurrentItem()) {
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
                    imm.hideSoftInputFromWindow(searchview.getWindowToken(), 0);
                }
                break;

            case R.id.search_btn_back:
                finish();
                break;
        }
    }

    private class SearchType {
        private final ResultFragment fragment;
        private final String title;

        SearchType(int type, String title) {
            this.title = title;
            this.fragment = new ResultFragment();
            Bundle b = new Bundle();
            b.putInt("type", type);
            fragment.setArguments(b);
        }
    }

    private class SearchViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        private SearchViewPagerAdapter(FragmentManager Fm) {
            super(Fm);
        }

        private void addFragment(Fragment fragment, String title) {
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
