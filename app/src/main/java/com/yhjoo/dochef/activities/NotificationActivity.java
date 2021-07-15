package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.NotificationListAdapter;
import com.yhjoo.dochef.databinding.ANotificationBinding;
import com.yhjoo.dochef.model.Notification;
import com.yhjoo.dochef.utils.DataGenerator;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    ANotificationBinding binding;

    ArrayList<Notification> notifications = new ArrayList<>();
    NotificationListAdapter notificationListAdapter;

    /*
        TODO
        1. firebase auth로 사용자 확인 하고, 쏠 수 있다면 FCM으로 날리기
        2. 서버에 저장할 필요는 없고, SQLITE에 저장
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ANotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.notificationToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.notificationSwipe.setOnRefreshListener(this);
        binding.notificationSwipe.setColorSchemeColors(this.getColor(R.color.colorPrimary));

        notifications = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_NOTIFICATION));

        notificationListAdapter = new NotificationListAdapter();
        notificationListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.notificationRecycler.getParent());
        notificationListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (notifications.get(position).getNotificationType() != getResources().getInteger(R.integer.NOTIFICATION_TYPE_2)) {
                startActivity(new Intent(NotificationActivity.this, RecipeDetailActivity.class));
            } else {
                Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
                intent.putExtra("MODE", HomeActivity.MODE.MY);
                startActivity(intent);
            }
        });
        binding.notificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.notificationRecycler.setAdapter(notificationListAdapter);
        notificationListAdapter.setNewData(notifications);
    }

    @Override
    public void onRefresh() {
        binding.notificationSwipe.setRefreshing(true);
        new Handler().postDelayed(() -> {
            notificationListAdapter.setNewData(notifications);
            notificationListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.notificationRecycler.getParent());
            binding.notificationSwipe.setRefreshing(false);
        }, 1000);
    }
}