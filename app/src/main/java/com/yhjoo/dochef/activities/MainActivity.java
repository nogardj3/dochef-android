package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.MainFragmentAdapter;
import com.yhjoo.dochef.databinding.AMainBinding;
import com.yhjoo.dochef.fragments.MainInitFragment;
import com.yhjoo.dochef.fragments.MainMyRecipeFragment;
import com.yhjoo.dochef.fragments.MainRecipesFragment;
import com.yhjoo.dochef.fragments.MainTimelineFragment;
import com.yhjoo.dochef.fragments.MainUserFragment;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import retrofit2.Response;

public class MainActivity extends BaseActivity{
    AMainBinding binding;
    FirebaseAnalytics mFirebaseAnalytics;
    RetrofitServices.UserService userService;
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
        alertdialog -> powermenu
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

        userService = RetrofitBuilder.create(this, RetrofitServices.UserService.class);

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

        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setIcon(R.drawable.ic_home_white));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setIcon(R.drawable.ic_hot_white));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setIcon(R.drawable.ic_favorite_white));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setIcon(R.drawable.ic_article_white));
        binding.mainTablayout.addTab(binding.mainTablayout.newTab().setIcon(R.drawable.ic_person_white));
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
            public void onTabUnselected(TabLayout.Tab tab) {            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        powerMenu = new PowerMenu.Builder(this)
                .addItem(new PowerMenuItem("최신순", true)) // add an item.
                .addItem(new PowerMenuItem("인기순", false)) // aad an item list.
                .addItem(new PowerMenuItem("별점순", false)) // aad an item list.
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(0f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSelectedTextColor(Color.WHITE)
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setBackgroundAlpha(0f)
                .build();
        powerMenu.setOnMenuItemClickListener((position, item) -> {
            if(position == 0)
                sortMenu(MainRecipesFragment.SORT.LATEST);
            else if(position == 1)
                sortMenu(MainRecipesFragment.SORT.POPULAR);
            else if(position == 2)
                sortMenu(MainRecipesFragment.SORT.RATING);
            powerMenu.setSelectedPosition(position);
            powerMenu.dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

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
        switch(item.getItemId()){
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

    void sortMenu(MainRecipesFragment.SORT sort){
        ((MainRecipesFragment) mainFragmentAdapter.getItem(1)).changeSortMode(sort);
    }
}