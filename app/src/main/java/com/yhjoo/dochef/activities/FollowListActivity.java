package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.UserBreif;
import com.yhjoo.dochef.databinding.AFollowlistBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class FollowListActivity extends BaseActivity {
    public enum MODE {FOLLOWER, FOLLOWING}

    AFollowlistBinding binding;

    UserListAdapter userListAdapter;

    String active_userid = "";
    String target_id = "";
    MODE current_mode = MODE.FOLLOWER;

    /*
        TODO
        1. Home 이후 -> 구현된 것 확인
        2. indexing 기능 보류
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AFollowlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.followlistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            active_userid = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null)).getString("user_id");
        } catch (Exception e) {
            Utils.log(e.toString());
        }

        target_id = getIntent().getStringExtra("userID");
        current_mode = (MODE) getIntent().getSerializableExtra("mode");

        userListAdapter = new UserListAdapter();
        userListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.followlistRecycler.getParent());

        if (current_mode == MODE.FOLLOWER)
            binding.followlistToolbar.setTitle("팔로워");
        else
            binding.followlistToolbar.setTitle("팔로잉");

        RetrofitServices.UserService userService =
                RetrofitBuilder.create(this, RetrofitServices.UserService.class);

        if (App.isServerAlive()) {
            if (current_mode == MODE.FOLLOWER) {
                userService.getFollowers(active_userid, target_id)
                        .enqueue(new BasicCallback<List<UserBreif>>(FollowListActivity.this) {
                            @Override
                            public void onResponse(Response<List<UserBreif>> response) {
                                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                                userListAdapter.setNewData(response.body());
                            }
                        });

                userListAdapter.setOnItemClickListener((adapter, view, position) -> {
                            Intent intent = new Intent(FollowListActivity.this, HomeActivity.class);
                            intent.putExtra("MODE", HomeActivity.MODE.USER);
                            intent.putExtra("userID", ((UserBreif) adapter.getData().get(position)).getUserID());
                            startActivity(intent);
                        }
                );
            } else if (current_mode == MODE.FOLLOWING) {
                userService.getFollowings(active_userid, target_id)
                        .enqueue(new BasicCallback<List<UserBreif>>(FollowListActivity.this) {
                            @Override
                            public void onResponse(Response<List<UserBreif>> response) {
                                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                                userListAdapter.setNewData(response.body());
                            }
                        });
            }
        } else {
            ArrayList<UserBreif> data = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_PROFILE));
            userListAdapter.setNewData(data);
        }

        binding.followlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.followlistRecycler.setAdapter(userListAdapter);
    }

    class UserListAdapter extends BaseQuickAdapter<UserBreif, BaseViewHolder> {
        UserListAdapter() {
            super(R.layout.li_follow);
        }

        @Override
        protected void convert(BaseViewHolder helper, UserBreif item) {
            if (!item.getUserImg().equals("default")) {
                Glide.with(mContext)
                        .load(App.isServerAlive()
                                ? getString(R.string.storage_image_url_profile) + item.getUserImg()
                                : Integer.valueOf(item.getUserImg()))
                        .into((AppCompatImageView) helper.getView(R.id.li_follow_userimg));
            }

            if(!item.getUserID().equals(active_userid)){
                if(item.getIs_follow() == 1)
                    helper.setVisible(R.id.li_followcancel_btn, true);
                else
                    helper.setVisible(R.id.li_follow_btn, true);
            }


            helper.setText(R.id.li_follow_nickname, item.getNickname());
        }
    }
}