package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.Notification;
import com.yhjoo.dochef.databinding.ANotificationBinding;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
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

        notifications = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_NOTIFICATION));

        notificationListAdapter = new NotificationListAdapter();
        notificationListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.notificationRecycler.getParent());
        notificationListAdapter.setOnLoadMoreListener(this, binding.notificationRecycler);
        notificationListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (notifications.get(position).getNotificationType() != getResources().getInteger(R.integer.NOTIFICATION_TYPE_2)){
                startActivity(new Intent(NotificationActivity.this, RecipeDetailActivity.class));
            }
            else{
                Intent intent =new Intent(NotificationActivity.this, HomeActivity.class);
//                intent.putExtra("userID", );
                startActivity(intent);
            }
        });
        notificationListAdapter.setNewData(notifications);
        notificationListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.notificationRecycler.getParent());
        notificationListAdapter.setEnableLoadMore(true);

        binding.notificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.notificationRecycler.setAdapter(notificationListAdapter);
    }

    @Override
    public void onLoadMoreRequested() {
        binding.notificationSwipe.setEnabled(false);
        notificationListAdapter.loadMoreEnd(true);
        binding.notificationSwipe.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        notificationListAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(() -> {
            notificationListAdapter.setNewData(notifications);
            binding.notificationSwipe.setRefreshing(false);
            notificationListAdapter.setEnableLoadMore(true);
        }, 1000);
    }


    class NotificationListAdapter extends BaseQuickAdapter<Notification, BaseViewHolder> {
        NotificationListAdapter() {
            super(R.layout.li_notification);
        }

        @Override
        protected void convert(BaseViewHolder helper, Notification item) {
            Glide.with(mContext)
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