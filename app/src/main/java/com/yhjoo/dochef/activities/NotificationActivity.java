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
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.views.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.yhjoo.dochef.Preferences.tempprofile;

public class NotificationActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private final ArrayList<Notification> notifications = new ArrayList<>();
    @BindView(R.id.notification_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.notification_recycler)
    RecyclerView recyclerView;
    private NotificationListAdapter notificationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_notification);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < 3; i++) {
            Random r = new Random();

            notifications.add(new Notification(Preferences.NOTIFICATION_TYPE_1, Integer.toString(tempprofile[r.nextInt(6)]), "xx", null, "12:00"));
            notifications.add(new Notification(Preferences.NOTIFICATION_TYPE_2, Integer.toString(tempprofile[r.nextInt(6)]), null, "밥", "8시간 전"));
            notifications.add(new Notification(Preferences.NOTIFICATION_TYPE_3, Integer.toString(tempprofile[r.nextInt(6)]), "yy", "개밥", "4월 14일"));
            notifications.add(new Notification(Preferences.NOTIFICATION_TYPE_4, Integer.toString(tempprofile[r.nextInt(6)]), "yy", "개밥", "4월 14일"));
            notifications.add(new Notification(Preferences.NOTIFICATION_TYPE_5, Integer.toString(tempprofile[r.nextInt(6)]), "yy", "개밥", "4월 14일"));
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(this.getColor(R.color.colorPrimary));
        notificationListAdapter = new NotificationListAdapter(Glide.with(this));
        notificationListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        notificationListAdapter.setOnLoadMoreListener(this, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notificationListAdapter);
        notificationListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (notifications.get(position).getType() != Preferences.NOTIFICATION_TYPE_2)
                startActivity(new Intent(NotificationActivity.this, RecipeActivity.class));
            else
                startActivity(new Intent(NotificationActivity.this, UserHomeActivity.class));
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

    private class Notification {
        private final int Type;
        private final String UserImg;
        private final String UserName;
        private final String RecipeName;
        private final String Time;

        Notification(int type, String userImg, String userName, String recipeName, String time) {
            Type = type;
            UserImg = userImg;
            UserName = userName;
            RecipeName = recipeName;
            Time = time;
        }

        private int getType() {
            return Type;
        }

        private String getUserImg() {
            return UserImg;
        }

        private String getUserName() {
            return UserName;
        }

        private String getRecipeName() {
            return RecipeName;
        }

        private String getTime() {
            return Time;
        }
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

            if (item.getType() == Preferences.NOTIFICATION_TYPE_1)
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype1, item.getUserName())));
            else if (item.getType() == Preferences.NOTIFICATION_TYPE_2)
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype2, item.getRecipeName())));
            else if (item.getType() == Preferences.NOTIFICATION_TYPE_3)
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype3, item.getUserName(), item.getRecipeName())));
            else if (item.getType() == Preferences.NOTIFICATION_TYPE_4)
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype4, item.getUserName(), item.getRecipeName())));
            else if (item.getType() == Preferences.NOTIFICATION_TYPE_5)
                helper.setText(R.id.li_notification_contents, Html.fromHtml(getString(R.string.notification_texttype5, item.getUserName(), item.getRecipeName())));
            helper.setText(R.id.li_notification_date, item.getTime());
        }
    }
}