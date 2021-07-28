package com.yhjoo.dochef.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePhase;

import java.util.ArrayList;
import java.util.List;

public class RecipeViewPagerAdapter extends FragmentPagerAdapter {
    final List<Fragment> fragments = new ArrayList<>();

    public RecipeViewPagerAdapter(FragmentManager Fm) {
        super(Fm);
    }

    public void addFragment(Fragment fragment, RecipePhase item) {
        Bundle b = new Bundle();
        b.putSerializable("item", item);
        fragment.setArguments(b);
        fragments.add(fragment);
    }


    public void addFragment(Fragment fragment, RecipeDetail item){
        Bundle b = new Bundle();
        b.putSerializable("item", item);
        fragment.setArguments(b);
        fragments.add(fragment);
    }

    public void addFragment(Fragment fragment, RecipePhase item,RecipeDetail item2){
        Bundle b = new Bundle();
        b.putSerializable("item", item);
        b.putSerializable("item2", item2);
        fragment.setArguments(b);
        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}