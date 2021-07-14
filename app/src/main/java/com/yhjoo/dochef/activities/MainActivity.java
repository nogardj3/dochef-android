package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.MainFragmentAdapter;
import com.yhjoo.dochef.databinding.AMainBinding;
import com.yhjoo.dochef.fragments.MainInitFragment;
import com.yhjoo.dochef.fragments.MainMyRecipeFragment;
import com.yhjoo.dochef.fragments.MainRecipesFragment;
import com.yhjoo.dochef.fragments.MainTimelineFragment;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    AMainBinding binding;
    FirebaseAnalytics mFirebaseAnalytics;
    MainFragmentAdapter mainFragmentAdapter;

    AppCompatTextView userName;
    AppCompatImageView userImage;

    /*
        TODO
        1. 마지막에 정리
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mainToolbar);

        MobileAds.initialize(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        binding.mainFab.setImageResource(R.drawable.ic_low_priority_white_24dp);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.mainDrawerlayout, binding.mainToolbar, 0, 0);
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
                        binding.mainFab.setImageResource(R.drawable.ic_create_white_24dp);
                        break;
                    case 3:
                        binding.mainViewpager.setCurrentItem(3);
                        binding.mainFam.hideMenu(false);
                        binding.mainFam.setVisibility(View.GONE);
                        binding.mainFab.setVisibility(View.VISIBLE);
                        binding.mainFab.setImageResource(R.drawable.ic_create_white_24dp);
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

        binding.famRecent.setOnClickListener(this::recentSort);
        binding.famPopular.setOnClickListener(this::popularSort);
        binding.mainFab.setOnClickListener(this::clickFab);

        userName = binding.mainNavigationview.getHeaderView(0).findViewById(R.id.navheader_nickname);
        userImage = binding.mainNavigationview.getHeaderView(0).findViewById(R.id.navheader_userimg);

        userName.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("MODE",HomeActivity.MODE.MY);
            startActivity(intent);
        });
        userImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("MODE",HomeActivity.MODE.MY);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();


        if (App.isServerAlive()) {
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Gson gson = new Gson();
            UserBrief userInfo = gson.fromJson(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null), UserBrief.class);

            Utils.log(userInfo.toString());
            Utils.log(!userInfo.getUserImg().equals("default"));

            userName.setText(userInfo.getNickname());

            if(!userInfo.getUserImg().equals("default"))
                Glide.with(this)
                        .load(getString(R.string.storage_image_url_profile) + userInfo.getUserImg())
                        .into(userImage);

            binding.mainNavigationview.getMenu().findItem(R.id.main_nav_myhome).setVisible(true);
            binding.mainNavigationview.getMenu().findItem(R.id.main_nav_myrecipe).setVisible(true);
            binding.mainNavigationview.getMenu().findItem(R.id.main_nav_notification).setVisible(true);
        } else {
            userName.setText("더미");

            binding.mainNavigationview.getMenu().findItem(R.id.main_nav_myhome).setVisible(false);
            binding.mainNavigationview.getMenu().findItem(R.id.main_nav_myrecipe).setVisible(false);
            binding.mainNavigationview.getMenu().findItem(R.id.main_nav_notification).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.mainDrawerlayout.isDrawerOpen(GravityCompat.START)) {
            binding.mainDrawerlayout.closeDrawer(GravityCompat.START);
        } else {
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
        int id = item.getItemId();
        if (id == R.id.main_menu_search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_nav_myhome) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("MODE",HomeActivity.MODE.MY);
            startActivity(intent);
        } else if (id == R.id.main_nav_myrecipe) {
            startActivity(new Intent(MainActivity.this, RecipeMyListActivity.class));
        } else if (id == R.id.main_nav_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        } else if (id == R.id.main_nav_setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void recentSort(View v){
        if (((MainRecipesFragment) mainFragmentAdapter.getItem(1)).getAlignMode() != MainRecipesFragment.mode_Recent)
            ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeAlignMode();

        binding.mainFam.close(true);
    }

    void popularSort(View v){
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
                Intent intent=new Intent(MainActivity.this, PostWriteActivity.class);
                intent.putExtra("MODE", PostWriteActivity.MODE.WRITE);
                startActivity(intent);
                break;
        }
    }
}