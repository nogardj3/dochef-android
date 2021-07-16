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

    NotificationListAdapter notificationListAdapter;

    ArrayList<Notification> notifications = new ArrayList<>();

    /*
        TODO
        액션 -> 서버보냄 -> 클라이언트에 보냄 -> 서버에서 오는거 받아서 뷰 처리
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

        notifications = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_NOTIFICATION));

        notificationListAdapter = new NotificationListAdapter();
        notificationListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.notificationRecycler.getParent());
        notificationListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (notifications.get(position).getNotificationType() != getResources().getInteger(R.integer.NOTIFICATION_TYPE_2)) {
                Intent intent = new Intent(NotificationActivity.this, RecipeDetailActivity.class);
//                .putExtra("recipeID", notifications.get(position).getRecipeID());
                startActivity(intent);
            } else {
                startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
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