package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.MainFragmentAdapter;
import com.yhjoo.dochef.databinding.AMainBinding;
import com.yhjoo.dochef.fragments.MainInitFragment;
import com.yhjoo.dochef.fragments.MainMyRecipeFragment;
import com.yhjoo.dochef.fragments.MainRecipesFragment;
import com.yhjoo.dochef.fragments.MainTimelineFragment;
import com.yhjoo.dochef.fragments.MainUserFragment;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    int[] tabIcons = new int[]{R.drawable.ic_home_white, R.drawable.ic_hot_white,
            R.drawable.ic_favorite_white, R.drawable.ic_article_white, R.drawable.ic_person_white};

    AMainBinding binding;
    FirebaseAnalytics mFirebaseAnalytics;
    MainFragmentAdapter mainFragmentAdapter;
    PowerMenu powerMenu;

    MenuItem menu_notification;
    MenuItem menu_search;
    MenuItem menu_sort;
    MenuItem menu_write_recipe;
    MenuItem menu_write_post;
    MenuItem menu_setting;

    String userID;

    /*
        TODO
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        MobileAds.initialize(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        userID = Utils.getUserBrief(this).getUserID();

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainInitFragment());
        fragments.add(new MainRecipesFragment());
        fragments.add(new MainMyRecipeFragment());
        fragments.add(new MainTimelineFragment());
        fragments.add(new MainUserFragment());

        mainFragmentAdapter = new MainFragmentAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                fragments);

        binding.mainViewpager.setOffscreenPageLimit(3);
        binding.mainViewpager.setPageMargin(15);
        binding.mainViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.mainTablayout));
        binding.mainViewpager.setAdapter(mainFragmentAdapter);

        for (int drawable : tabIcons) {
            binding.mainTablayout.addTab(binding.mainTablayout.newTab().setIcon(drawable));
        }

        binding.mainTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.mainViewpager.setCurrentItem(tab.getPosition());

                menu_notification.setVisible(false);
                menu_sort.setVisible(false);
                menu_write_recipe.setVisible(false);
                menu_write_post.setVisible(false);
                menu_search.setVisible(false);
                menu_setting.setVisible(false);
                switch (tab.getPosition()) {
                    case 0:
                        menu_notification.setVisible(true);
                        break;
                    case 1:
                        menu_sort.setVisible(true);
                        menu_search.setVisible(true);
                        break;
                    case 2:
                        menu_write_recipe.setVisible(true);
                        menu_search.setVisible(true);
                        break;
                    case 3:
                        menu_write_post.setVisible(true);
                        menu_search.setVisible(true);
                        break;
                    case 4:
                        menu_setting.setVisible(true);
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

        powerMenu = new PowerMenu.Builder(this)
                .addItem(new PowerMenuItem("최신순", true))
                .addItem(new PowerMenuItem("인기순", false))
                .addItem(new PowerMenuItem("별점순", false))
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(0f)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSelectedTextColor(Color.WHITE)
                .setTextGravity(Gravity.CENTER)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setBackgroundAlpha(0f)
                .build();

        powerMenu.setOnMenuItemClickListener((position, item) -> {
            if (position == 0)
                sortMenu(MainRecipesFragment.SORT.LATEST);
            else if (position == 1)
                sortMenu(MainRecipesFragment.SORT.POPULAR);
            else if (position == 2)
                sortMenu(MainRecipesFragment.SORT.RATING);
            powerMenu.setSelectedPosition(position);
            powerMenu.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menu_notification = menu.findItem(R.id.main_menu_notification);
        menu_sort = menu.findItem(R.id.main_menu_sort);
        menu_write_recipe = menu.findItem(R.id.main_menu_write_recipe);
        menu_write_post = menu.findItem(R.id.main_menu_write_post);
        menu_search = menu.findItem(R.id.main_menu_search);
        menu_setting = menu.findItem(R.id.main_menu_setting);

        menu_sort.setVisible(false);
        menu_write_recipe.setVisible(false);
        menu_write_post.setVisible(false);
        menu_search.setVisible(false);
        menu_setting.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_notification:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
            case R.id.main_menu_sort:
                powerMenu.showAsAnchorRightBottom(binding.mainToolbar);
                break;
            case R.id.main_menu_write_recipe:
                startActivity(new Intent(this, RecipeMakeActivity.class));
                break;
            case R.id.main_menu_write_post:
                Intent intent = new Intent(this, PostWriteActivity.class)
                        .putExtra("MODE", PostWriteActivity.MODE.WRITE);
                startActivity(intent);
                break;
            case R.id.main_menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.main_menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void sortMenu(MainRecipesFragment.SORT sort) {
        ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeSortMode(sort);
    }
}