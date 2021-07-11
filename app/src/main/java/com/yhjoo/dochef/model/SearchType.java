package com.yhjoo.dochef.model;

import android.os.Bundle;

import com.yhjoo.dochef.fragments.ResultFragment;

public class SearchType {
    ResultFragment fragment;
    String title;

    public SearchType(int type, String title) {
        this.title = title;
        this.fragment = new ResultFragment();
        Bundle b = new Bundle();
        b.putInt("type", type);
        fragment.setArguments(b);
    }

    public ResultFragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }
}