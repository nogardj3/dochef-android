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
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.MainFragmentAdapter;
import com.yhjoo.dochef.fragments.MainInitFragment;
import com.yhjoo.dochef.fragments.MainMyRecipeFragment;
import com.yhjoo.dochef.fragments.MainRecipesFragment;
import com.yhjoo.dochef.fragments.MainTimelineFragment;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private AppCompatTextView userName;
    private AppCompatImageView userImage;

    private MainFragmentAdapter mainFragmentAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;

    /*
        TODO
        1. floating action menu 버튼 못바꾸나
        2. 마지막에 정리할거임
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        ButterKnife.bind(this);
        MobileAds.initialize(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


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

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainInitFragment());
        fragments.add(new MainRecipesFragment());
        fragments.add(new MainMyRecipeFragment());
        fragments.add(new MainTimelineFragment());

        mainFragmentAdapter = new MainFragmentAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                fragments);

        viewPager.setAdapter(mainFragmentAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[0]));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[1]));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[2]));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getStringArray(R.array.main_menu)[3]));

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

        userName = (AppCompatTextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_nickname);
        userImage = (AppCompatImageView) navigationView.getHeaderView(0).findViewById(R.id.navheader_userimg);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (App.isServerAlive()) {
            userName.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("MODE",HomeActivity.MODE.MY);
                startActivity(intent);
            });

            try {
                SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                JSONObject userInfo = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null));

                Utils.log(userInfo.toString());

                userName.setText(userInfo.getString("nickname"));

                Glide.with(this)
                        .load(getString(R.string.storage_image_url_profile) + userInfo.get("profile_image_url"))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_default_profile))
                        .into(userImage);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            navigationView.getMenu().findItem(R.id.main_nav_myhome).setVisible(true);
            navigationView.getMenu().findItem(R.id.main_nav_myrecipe).setVisible(true);
            navigationView.getMenu().findItem(R.id.main_nav_notification).setVisible(true);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_default_profile)
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);

            userImage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AccountActivity.class)));
            userName.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AccountActivity.class)));
            userName.setText("가나다라마바");

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
            startActivity(new Intent(MainActivity.this, RecipeListActivity.class));
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
                    if (((MainRecipesFragment) mainFragmentAdapter.getItem(1)).getAlignMode() != MainRecipesFragment.mode_Recent)
                        ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeAlignMode();
                } else if (v.getId() == R.id.fam_popular) {
                    if (((MainRecipesFragment) mainFragmentAdapter.getItem(1)).getAlignMode() != MainRecipesFragment.mode_Popular)
                        ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeAlignMode();
                }
                floatingActionMenu.close(true);

                break;
            case 2:
                startActivity(new Intent(MainActivity.this, RecipeMakeActivity.class));
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, PostWriteActivity.class));
                break;
        }
    }


}