package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.UserList;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FollowListActivity extends BaseActivity {
    @BindView(R.id.followlist_recycler)
    RecyclerView recyclerView;
    MODE current_mode = MODE.FOLLOWER;
    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_followlist);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.followlist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userID = getIntent().getStringExtra("UserID");
        current_mode = (MODE) getIntent().getSerializableExtra("mode");

        userListAdapter = new UserListAdapter(Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userListAdapter);

        userListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());

        if (App.isServerAlive()) {
            if (current_mode == MODE.FOLLOWER) {
                toolbar.setTitle("팔로워");
                RetrofitServices.FollowerService followerService = new Retrofit.Builder()
                        .baseUrl(getString(R.string.server_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(RetrofitServices.FollowerService.class);

                followerService.GetFollowerCall(userID, 0)
                        .enqueue(new BasicCallback<List<UserList>>(FollowListActivity.this) {
                            @Override
                            public void onResponse(Response<List<UserList>> response) {
                                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                                userListAdapter.setNewData(response.body());
                            }
                        });

                userListAdapter.setOnItemClickListener((adapter, view, position) -> {
                            Intent intent = new Intent(FollowListActivity.this, HomeUserActivity.class)
                                    .putExtra("UserID", ((UserList) adapter.getData().get(position)).getUserID());
                            startActivity(intent);
                        }
                );
            } else if (current_mode == MODE.FOLLOWING) {
                toolbar.setTitle("팔로잉");
                RetrofitServices.FollowingService followingService = new Retrofit.Builder()
                        .baseUrl(getString(R.string.server_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(RetrofitServices.FollowingService.class);

                followingService.GetFollowingCall(userID, 0)
                        .enqueue(new BasicCallback<List<UserList>>(FollowListActivity.this) {
                            @Override
                            public void onResponse(Response<List<UserList>> response) {
                                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                                userListAdapter.setNewData(response.body());
                            }
                        });
            }
        } else {
            if (current_mode == MODE.FOLLOWER) {
                toolbar.setTitle("팔로워");
                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                ArrayList<UserList> data = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_PROFILE));
                userListAdapter.setNewData(data);
            } else if (current_mode == MODE.FOLLOWING) {
                toolbar.setTitle("팔로잉");
                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                ArrayList<UserList> data = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_PROFILE));
                userListAdapter.setNewData(data);
            }
        }
    }

    /*
        TODO
        1. retrofit 구현
        2. follower, following 모드에 따라 다른 기능 구현
    */

    public enum MODE {FOLLOWER, FOLLOWING}

    private class UserListAdapter extends BaseQuickAdapter<UserList, BaseViewHolder> {
        private final RequestManager requestManager;

        UserListAdapter(RequestManager requestManager) {
            super(R.layout.li_follow);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, UserList item) {
            requestManager
                    .load(App.isServerAlive()
                            ? "https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + item.getUserImg()
                            : Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).circleCrop())
                    .into((AppCompatImageView) helper.getView(R.id.li_follow_userimg));

            helper.setText(R.id.li_follow_nickname, item.getNickname());
        }
    }
}