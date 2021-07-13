package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.FollowListAdapter;
import com.yhjoo.dochef.databinding.AFollowlistBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class FollowListActivity extends BaseActivity {
    public enum MODE {FOLLOWER, FOLLOWING}

    AFollowlistBinding binding;
    RetrofitServices.UserService userService;
    FollowListAdapter followListAdapter;

    String active_userid = "";
    String target_id = "";
    MODE current_mode = MODE.FOLLOWER;

    /*
        TODO
        1. 실행 해보고 수정할거 수정하기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AFollowlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.followlistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService = RetrofitBuilder.create(this, RetrofitServices.UserService.class);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            active_userid = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null)).getString("user_id");
        } catch (Exception e) {
            Utils.log(e.toString());
        }

        target_id = getIntent().getStringExtra("userID");
        current_mode = (MODE) getIntent().getSerializableExtra("mode");

        binding.followlistToolbar.setTitle(current_mode == MODE.FOLLOWER ? "팔로워" : "팔로잉");

        followListAdapter = new FollowListAdapter(active_userid);
        followListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.followlistRecycler.getParent());
        followListAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Intent intent = new Intent(FollowListActivity.this, HomeActivity.class);
                    intent.putExtra("MODE", HomeActivity.MODE.USER);
                    intent.putExtra("userID", ((UserBrief) adapter.getData().get(position)).getUserID());
                    startActivity(intent);
                }
        );

        if (App.isServerAlive()) {
            if (current_mode == MODE.FOLLOWER)
                getFollower();
            else if (current_mode == MODE.FOLLOWING)
                getFollowing();
        } else {
            ArrayList<UserBrief> data = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_USER));
            followListAdapter.setNewData(data);
        }

        binding.followlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.followlistRecycler.setAdapter(followListAdapter);
    }

    void getFollower(){
        userService.getFollowers(active_userid, target_id)
                .enqueue(new BasicCallback<ArrayList<UserBrief>>(FollowListActivity.this) {
                    @Override
                    public void onResponse(Response<ArrayList<UserBrief>> response) {
                        followListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                        followListAdapter.setNewData(response.body());
                    }
                });
    }

    void getFollowing(){
        userService.getFollowings(active_userid, target_id)
                .enqueue(new BasicCallback<ArrayList<UserBrief>>(FollowListActivity.this) {
                    @Override
                    public void onResponse(Response<ArrayList<UserBrief>> response) {
                        followListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                        followListAdapter.setNewData(response.body());
                    }
                });
    }
}