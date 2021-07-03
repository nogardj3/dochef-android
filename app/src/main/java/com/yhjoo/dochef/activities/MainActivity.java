package com.yhjoo.dochef.activities;

import android.content.DialogInterface;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.fragments.MainFragment;
import com.yhjoo.dochef.fragments.MyRecipeFragment;
import com.yhjoo.dochef.fragments.RecipeFragment;
import com.yhjoo.dochef.fragments.TimeLineFragment;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigationview)
    NavigationView navigationView;
    @BindView(R.id.main_viewpager)
    ViewPager viewPager;
    @BindView(R.id.main_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.main_fam)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.main_fab)
    FloatingActionButton floatingActionButton;

    private AppCompatTextView username;
    private AppCompatImageView userImage;

    private TabPagerAdapter tabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        ButterKnife.bind(this);
        MobileAds.initialize(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        floatingActionButton.setImageResource(R.drawable.ic_low_priority_white_24dp);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), new ArrayList<>(Arrays.asList(new MainFragment(), new RecipeFragment(), new MyRecipeFragment(), new TimeLineFragment())));
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.addTab(tabLayout.newTab().setText("메인"));
        tabLayout.addTab(tabLayout.newTab().setText("레시피"));
        tabLayout.addTab(tabLayout.newTab().setText("내 레시피"));
        tabLayout.addTab(tabLayout.newTab().setText("타임라인"));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setPageMargin(15);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        floatingActionMenu.hideMenu(false);
                        floatingActionMenu.setVisibility(View.GONE);
                        floatingActionButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        floatingActionMenu.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.GONE);
                        floatingActionButton.setImageResource(R.drawable.ic_low_priority_white_24dp);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        floatingActionMenu.hideMenu(false);
                        floatingActionMenu.setVisibility(View.GONE);
                        floatingActionButton.setVisibility(View.VISIBLE);
                        floatingActionButton.setImageResource(R.drawable.ic_create_white_24dp);
                        break;
                    case 3:
                        viewPager.setCurrentItem(3);
                        floatingActionMenu.hideMenu(false);
                        floatingActionMenu.setVisibility(View.GONE);
                        floatingActionButton.setVisibility(View.VISIBLE);
                        floatingActionButton.setImageResource(R.drawable.ic_create_white_24dp);
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

        username = (AppCompatTextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_nickname);
        userImage = (AppCompatImageView) navigationView.getHeaderView(0).findViewById(R.id.navheader_userimg);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (mSharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false)) {
            username.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MyHomeActivity.class)));

            try {
                JSONObject userInfo = new JSONObject(mSharedPreferences.getString(Preferences.SHAREDPREFERENCE_USERINFO, null));

                username.setText(userInfo.getString("NICKNAME"));

                Glide.with(this)
                        .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + userInfo.get("PROFILE_IMAGE"))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).circleCrop())
                        .into(userImage);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            navigationView.getMenu().findItem(R.id.main_nav_myhome).setVisible(true);
            navigationView.getMenu().findItem(R.id.main_nav_myrecipe).setVisible(true);
            navigationView.getMenu().findItem(R.id.main_nav_notification).setVisible(true);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_person_black_24dp)
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);

            userImage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
            username.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
            username.setText("가나다라마바");

            navigationView.getMenu().findItem(R.id.main_nav_myhome).setVisible(false);
            navigationView.getMenu().findItem(R.id.main_nav_myrecipe).setVisible(false);
            navigationView.getMenu().findItem(R.id.main_nav_notification).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("종료하시겠습니까?")
                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
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
            startActivity(new Intent(MainActivity.this, MyHomeActivity.class));
        } else if (id == R.id.main_nav_myrecipe) {
            startActivity(new Intent(MainActivity.this, MyRecipeActivity.class));
        } else if (id == R.id.main_nav_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        } else if (id == R.id.main_nav_setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.main_fab, R.id.fam_recent, R.id.fam_popular})
    void onclick(View v) {
        switch (viewPager.getCurrentItem()) {
            case 1:
                if (v.getId() == R.id.fam_recent) {
                    if (((RecipeFragment) tabPagerAdapter.getItem(1)).getAlignMode() != RecipeFragment.mode_Recent)
                        ((RecipeFragment) tabPagerAdapter.getItem(1)).changeAlignMode();
                } else if (v.getId() == R.id.fam_popular) {
                    if (((RecipeFragment) tabPagerAdapter.getItem(1)).getAlignMode() != RecipeFragment.mode_Popular)
                        ((RecipeFragment) tabPagerAdapter.getItem(1)).changeAlignMode();
                }
                floatingActionMenu.close(true);

                break;
            case 2:
                startActivity(new Intent(MainActivity.this, MakeRecipeActivity.class));
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, WritePostActivity.class));
                break;
        }
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList;

        private TabPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}