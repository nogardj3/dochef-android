package quvesoft.project2.views;


import com.chad.library.adapter.base.loadmore.LoadMoreView;

import quvesoft.project2.R;

public final class CustomLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.rv_loadmore;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.loadmore_loading;
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
