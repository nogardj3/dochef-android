package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.MainFragmentAdapter;
import com.yhjoo.dochef.databinding.AMainBinding;
import com.yhjoo.dochef.fragments.MainInitFragment;
import com.yhjoo.dochef.fragments.MainMyRecipeFragment;
import com.yhjoo.dochef.fragments.MainRecipesFragment;
import com.yhjoo.dochef.fragments.MainTimelineFragment;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import retrofit2.Response;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    AMainBinding binding;
    FirebaseAnalytics mFirebaseAnalytics;
    RetrofitServices.UserService userService;
    MainFragmentAdapter mainFragmentAdapter;

    AppCompatTextView userName;
    AppCompatImageView userImage;

    UserDetail userDetailInfo;
    String userID;


    /*
        TODO
        로고 아이콘빼고 텍스트로
        fragment adapter
            bottom으로 바꾸기
            floating action button -> menu item
            fragment추가 -> drawer에 있는거 옮기기 (알림은 위로 올라간다)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        MobileAds.initialize(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        userService = RetrofitBuilder.create(this, RetrofitServices.UserService.class);

        userID = Utils.getUserBrief(this).getUserID();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.mainDrawerlayout,
                binding.mainToolbar, 0, 0);
        binding.mainDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.mainNavigationview.setNavigationItemSelectedListener(this);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainInitFragment());
        fragments.add(new MainRecipesFragment());
        fragments.add(new MainMyRecipeFragment());
        fragments.add(new MainTimelineFragment());

        mainFragmentAdapter = new MainFragmentAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                fragments);

        binding.mainViewpager.setOffscreenPageLimit(3);
        binding.mainViewpager.setPageMargin(15);
        binding.mainViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.mainTablayout));
        binding.mainViewpager.setAdapter(mainFragmentAdapter);

        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[0]));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[1]));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[2]));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[3]));
        binding.mainTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        binding.mainViewpager.setCurrentItem(0);
                        binding.mainFam.hideMenu(false);
                        binding.mainFam.setVisibility(View.GONE);
                        binding.mainFab.setVisibility(View.GONE);
                        break;
                    case 1:
                        binding.mainViewpager.setCurrentItem(1);
                        binding.mainFam.setVisibility(View.VISIBLE);
                        binding.mainFab.setVisibility(View.GONE);
                        binding.mainFab.setImageResource(R.drawable.ic_low_priority_white_24dp);
                        break;
                    case 2:
                        binding.mainViewpager.setCurrentItem(2);
                        binding.mainFam.hideMenu(false);
                        binding.mainFam.setVisibility(View.GONE);
                        binding.mainFab.setVisibility(View.VISIBLE);
                        binding.mainFab.setImageResource(R.drawable.ic_create_white);
                        break;
                    case 3:
                        binding.mainViewpager.setCurrentItem(3);
                        binding.mainFam.hideMenu(false);
                        binding.mainFam.setVisibility(View.GONE);
                        binding.mainFab.setVisibility(View.VISIBLE);
                        binding.mainFab.setImageResource(R.drawable.ic_create_white);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.mainFab.setImageResource(R.drawable.ic_low_priority_white_24dp);

        binding.famRecent.setOnClickListener(this::recentSort);
        binding.famPopular.setOnClickListener(this::popularSort);
        binding.mainFab.setOnClickListener(this::clickFab);

        userName = binding.mainNavigationview.getHeaderView(0).findViewById(R.id.navheader_nickname);
        userImage = binding.mainNavigationview.getHeaderView(0).findViewById(R.id.navheader_userimg);
        userName.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        });
        userImage.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (App.isServerAlive()) {
            getUserDetail();
        } else {
            userDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_DETAIL));

            ImageLoadUtil.loadUserImage(this, userDetailInfo.getUserImg(), userImage);

            userName.setText(userDetailInfo.getNickname());
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.mainDrawerlayout.isDrawerOpen(GravityCompat.START))
            binding.mainDrawerlayout.closeDrawer(GravityCompat.START);
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("종료하시겠습니까?")
                    .setPositiveButton("종료", (dialog, which) -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getString(R.string.analytics_id_terminated));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getString(R.string.analytics_name_terminated));
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, getString(R.string.analytics_type_text));
                        mFirebaseAnalytics.logEvent(getString(R.string.analytics_event_terminated), bundle);

                        dialog.dismiss();
                        finish();
                    })
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_nav_myhome) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (id == R.id.main_nav_myrecipe) {
            Intent intent = new Intent(this, RecipeMyListActivity.class)
                    .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        } else if (id == R.id.main_nav_notification)
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        else if (id == R.id.main_nav_setting)
            startActivity(new Intent(MainActivity.this, SettingActivity.class));

        binding.mainDrawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    void recentSort(View v) {
        if (((MainRecipesFragment) mainFragmentAdapter.getItem(1)).getAlignMode() != MainRecipesFragment.mode_Recent)
            ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeAlignMode();

        binding.mainFam.close(true);
    }

    void popularSort(View v) {
        if (((MainRecipesFragment) mainFragmentAdapter.getItem(1)).getAlignMode() != MainRecipesFragment.mode_Popular)
            ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeAlignMode();

        binding.mainFam.close(true);
    }

    void clickFab(View v) {
        switch (binding.mainViewpager.getCurrentItem()) {
            case 2:
                startActivity(new Intent(MainActivity.this, RecipeMakeActivity.class));
                break;
            case 3:
                Intent intent = new Intent(MainActivity.this, PostWriteActivity.class)
                        .putExtra("MODE", PostWriteActivity.MODE.WRITE);
                startActivity(intent);
                break;
        }
    }

    void getUserDetail() {
        userService.getUserDetail(userID)
                .enqueue(new BasicCallback<UserDetail>(this) {
                    @Override
                    public void onResponse(Response<UserDetail> response) {
                        userDetailInfo = response.body();

                        ImageLoadUtil.loadUserImage(
                                MainActivity.this, userDetailInfo.getUserImg(), userImage);

                        userName.setText(userDetailInfo.getNickname());
                    }
                });
    }
}