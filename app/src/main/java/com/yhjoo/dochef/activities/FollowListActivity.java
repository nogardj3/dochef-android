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
import com.yhjoo.dochef.databinding.AFollowlistBinding;
import com.yhjoo.dochef.databinding.AReviewBinding;
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
    public enum MODE {FOLLOWER, FOLLOWING}

    AFollowlistBinding binding;

    UserListAdapter userListAdapter;

    MODE current_mode = MODE.FOLLOWER;


    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
        3. follower, following 분기
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AFollowlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.followlistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userID = getIntent().getStringExtra("UserID");
        current_mode = (MODE) getIntent().getSerializableExtra("mode");

        userListAdapter = new UserListAdapter();
        binding.followlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.followlistRecycler.setAdapter(userListAdapter);

        userListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.followlistRecycler.getParent());

        if (App.isServerAlive()) {
            if (current_mode == MODE.FOLLOWER) {
                binding.followlistToolbar.setTitle("팔로워");
                RetrofitServices.FollowerService followerService = new Retrofit.Builder()
                        .baseUrl(getString(R.string.server_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(RetrofitServices.FollowerService.class);

                followerService.GetFollowerCall(userID, 0)
                        .enqueue(new BasicCallback<List<UserList>>(FollowListActivity.this) {
                            @Override
                            public void onResponse(Response<List<UserList>> response) {
                                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
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
                binding.followlistToolbar.setTitle("팔로잉");
                RetrofitServices.FollowingService followingService = new Retrofit.Builder()
                        .baseUrl(getString(R.string.server_url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(RetrofitServices.FollowingService.class);

                followingService.GetFollowingCall(userID, 0)
                        .enqueue(new BasicCallback<List<UserList>>(FollowListActivity.this) {
                            @Override
                            public void onResponse(Response<List<UserList>> response) {
                                userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
                                userListAdapter.setNewData(response.body());
                            }
                        });
            }
        } else {
            if (current_mode == MODE.FOLLOWER) {
                binding.followlistToolbar.setTitle("팔로워");
                ArrayList<UserList> data = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_PROFILE));
                userListAdapter.setNewData(data);
            } else if (current_mode == MODE.FOLLOWING) {
                binding.followlistToolbar.setTitle("팔로잉");
                ArrayList<UserList> data = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_PROFILE));
                userListAdapter.setNewData(data);
            }
        }
    }

    class UserListAdapter extends BaseQuickAdapter<UserList, BaseViewHolder> {
        UserListAdapter() {
            super(R.layout.li_follow);
        }

        @Override
        protected void convert(BaseViewHolder helper, UserList item) {
            Glide.with(mContext)
                    .load(App.isServerAlive()
                            ? "getString(R.string.profile_image_storage_url)" + item.getUserImg()
                            : Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_default_profile).circleCrop())
                    .into((AppCompatImageView) helper.getView(R.id.li_follow_userimg));

            helper.setText(R.id.li_follow_nickname, item.getNickname());
        }
    }
}