package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Notification;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.views.CustomLoadMoreView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.notification_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.notification_recycler)
    RecyclerView recyclerView;

    private ArrayList<Notification> notifications = new ArrayList<>();
    private NotificationListAdapter notificationListAdapter;

    /*
        TODO
        1. firebase auth로 사용자 확인 하고, 쏠 수 있다면 FCM으로 날리기
        2. 서버에 저장할 필요는 없고, SQLITE에 저장
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_notification);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notifications = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_NOTIFICATION));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(this.getColor(R.color.colorPrimary));
        notificationListAdapter = new NotificationListAdapter(Glide.with(this));
        notificationListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        notificationListAdapter.setOnLoadMoreListener(this, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationListAdapter);
        notificationListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (notifications.get(position).getNotificationType() != getResources().getInteger(R.integer.NOTIFICATION_TYPE_2))
                startActivity(new Intent(NotificationActivity.this, RecipeDetailActivity.class));
            else
                startActivity(new Intent(NotificationActivity.this, HomeUserActivity.class));
        });
        notificationListAdapter.setNewData(notifications);
        notificationListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
        notificationListAdapter.setLoadMoreView(new CustomLoadMoreView());
        notificationListAdapter.setEnableLoadMore(true);
    }

    @Override
    public void onLoadMoreRequested() {
        swipeRefreshLayout.setEnabled(false);
        notificationListAdapter.loadMoreEnd(true);
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        notificationListAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(() -> {
            notificationListAdapter.setNewData(notifications);
            swipeRefreshLayout.setRefreshing(false);
            notificationListAdapter.setEnableLoadMore(true);
        }, 1000);
    }


    private class NotificationListAdapter extends BaseQuickAdapter<Notification, BaseViewHolder> {
        private final RequestManager requestManager;

        NotificationListAdapter(RequestManager requestManager) {
            super(R.layout.li_notification);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, Notification item) {
            requestManager
                    .load(Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.circleCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_notification_userimg));

            if (item.getNotificationType() == getResources().getInteger(R.integer.NOTIFICATION_TYPE_1))
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype1, item.getUserName()),Html.FROM_HTML_MODE_LEGACY));
            else if (item.getNotificationType() == getResources().getInteger(R.integer.NOTIFICATION_TYPE_2))
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype2, item.getRecipeName()),Html.FROM_HTML_MODE_LEGACY));
            else if (item.getNotificationType() == getResources().getInteger(R.integer.NOTIFICATION_TYPE_3))
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype3, item.getUserName(), item.getRecipeName()),Html.FROM_HTML_MODE_LEGACY));
            else if (item.getNotificationType() == getResources().getInteger(R.integer.NOTIFICATION_TYPE_4))
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype4, item.getUserName(), item.getRecipeName()),Html.FROM_HTML_MODE_LEGACY));
            else if (item.getNotificationType() == getResources().getInteger(R.integer.NOTIFICATION_TYPE_5))
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype5, item.getUserName(), item.getRecipeName()),Html.FROM_HTML_MODE_LEGACY));
            helper.setText(R.id.li_notification_date, item.getDateTime());
        }
    }
}