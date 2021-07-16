package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.FollowListAdapter;
import com.yhjoo.dochef.databinding.AFollowlistBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import retrofit2.Response;

public class FollowListActivity extends BaseActivity {
    public enum MODE {FOLLOWER, FOLLOWING}

    AFollowlistBinding binding;
    RetrofitServices.UserService userService;
    FollowListAdapter followListAdapter;

    MODE current_mode = MODE.FOLLOWER;
    ArrayList<String> activeUserFollow;
    String active_userid;
    String target_id;

    /*
        TODO
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AFollowlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.followlistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService = RetrofitBuilder.create(this, RetrofitServices.UserService.class);

        active_userid = Utils.getUserBrief(this).getUserID();
        target_id = getIntent().getStringExtra("userID");
        current_mode = (MODE) getIntent().getSerializableExtra("MODE");

        binding.followlistToolbar.setTitle(current_mode == MODE.FOLLOWER ? "팔로워" : "팔로잉");
        followListAdapter = new FollowListAdapter(active_userid);
        followListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.followlistRecycler.getParent());
        followListAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Utils.log(((UserBrief) adapter.getData().get(position)).getUserID());
                    Intent intent = new Intent(FollowListActivity.this, HomeActivity.class)
                            .putExtra("userID", ((UserBrief) adapter.getData().get(position)).getUserID());
                    startActivity(intent);
                }
        );
        followListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                    String target = ((UserBrief) adapter.getData().get(position)).getUserID();
                    if (view.getId() == R.id.user_followcancel_btn)
                        subscirbeUser(target);
                    else if (view.getId() == R.id.user_follow_btn)
                        unsubscirbeUser(target);

                }
        );
        binding.followlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.followlistRecycler.setAdapter(followListAdapter);


        if (App.isServerAlive())
            getActiveUserInfo();
        else {
            ArrayList<UserBrief> data = DataGenerator.make(getResources(), getResources().getInteger(R.integer.LOCAL_TYPE_USER_BRIEF));
            activeUserFollow = data.get(0).getFollow();
            followListAdapter.setActiveUserFollow(activeUserFollow);

            if (current_mode == MODE.FOLLOWER)
                getFollower();
            else if (current_mode == MODE.FOLLOWING)
                getFollowing();
        }
    }

    void getActiveUserInfo() {
        userService.getUserDetail(active_userid)
                .enqueue(new BasicCallback<UserDetail>(this) {
                    @Override
                    public void onResponse(Response<UserDetail> response) {
                        activeUserFollow = response.body().getFollow();
                        Utils.log(activeUserFollow.toString());

                        followListAdapter.setActiveUserFollow(activeUserFollow);
                        if (current_mode == MODE.FOLLOWER)
                            getFollower();
                        else if (current_mode == MODE.FOLLOWING)
                            getFollowing();
                    }
                });
    }

    void getFollower() {
        userService.getFollowers(target_id)
                .enqueue(new BasicCallback<ArrayList<UserBrief>>(this) {
                    @Override
                    public void onResponse(Response<ArrayList<UserBrief>> response) {
                        Utils.log(activeUserFollow.toString());

                        followListAdapter.setNewData(response.body());
                        followListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                    }
                });
    }

    void getFollowing() {
        userService.getFollowings(target_id)
                .enqueue(new BasicCallback<ArrayList<UserBrief>>(this) {
                    @Override
                    public void onResponse(Response<ArrayList<UserBrief>> response) {
                        followListAdapter.setNewData(response.body());
                        followListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                    }
                });
    }

    void subscirbeUser(String target_id) {
        userService.subscribeUser(active_userid, target_id)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Response<JsonObject> response) {
                        getActiveUserInfo();
                    }
                });
    }

    void unsubscirbeUser(String target_id) {
        userService.unsubscribeUser(active_userid, target_id)
                .enqueue(new BasicCallback<JsonObject>(this) {
                    @Override
                    public void onResponse(Response<JsonObject> response) {
                        getActiveUserInfo();
                    }
                });
    }
}