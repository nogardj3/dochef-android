package com.yhjoo.dochef.views;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.yhjoo.dochef.R;

public final class CustomLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.rv_loadmore;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loading_progress_group;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.loadmore_fail;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.loadmore_end;
    }
}
