package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.JsonObject;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.FollowListAdapter;
import com.yhjoo.dochef.databinding.AFollowlistBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Response;

public class FollowListActivity extends BaseActivity {
    public enum MODE {FOLLOWER, FOLLOWING}

    AFollowlistBinding binding;
    RxRetrofitServices.UserService rxUserService;
    FollowListAdapter followListAdapter;

    MODE current_mode = MODE.FOLLOWER;
    UserDetail userDetailInfo;
    ArrayList<UserBrief> userList;

    String active_userid;
    String target_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AFollowlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.followlistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rxUserService = RxRetrofitBuilder.create(this, RxRetrofitServices.UserService.class);

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
        followListAdapter.setOnItemChildClickListener(this::onListItemClick);
        binding.followlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.followlistRecycler.setAdapter(followListAdapter);

        if (App.isServerAlive()){
            Single<Response<ArrayList<UserBrief>>> modeSingle;
            if (current_mode == MODE.FOLLOWER)
                modeSingle = rxUserService.getFollowers(target_id)
                        .observeOn(AndroidSchedulers.mainThread());
            else
                modeSingle = rxUserService.getFollowings(target_id)
                        .observeOn(AndroidSchedulers.mainThread());

            compositeDisposable.add(
                    rxUserService.getUserDetail(active_userid)
                            .flatMap((Function<Response<UserDetail>, Single<Response<ArrayList<UserBrief>>>>)
                                    response -> {
                                        userDetailInfo = response.body();
                                        return modeSingle;
                                    }
                            )
                            .subscribe(response->{
                                userList = response.body();
                                setListData();
                            }, RxRetrofitBuilder.defaultConsumer())
            );
        }
        else {
            userDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_DETAIL));
            userList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_BRIEF));

            setListData();
        }
    }

    void onListItemClick(BaseQuickAdapter adapter, View view, int position) {
        String target = ((UserBrief) adapter.getData().get(position)).getUserID();

        Single<Response<JsonObject>> subORunsub = view.getId() == R.id.user_followcancel_btn ?
                rxUserService.subscribeUser(active_userid, target):
                rxUserService.unsubscribeUser(active_userid, target);

        Single<Response<ArrayList<UserBrief>>> modeSingle = current_mode == MODE.FOLLOWER ?
                rxUserService.getFollowers(target_id)
                        .observeOn(AndroidSchedulers.mainThread()):
                rxUserService.getFollowings(target_id)
                        .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(
                subORunsub
                        .flatMap((Function<Response<JsonObject>, Single<Response<UserDetail>>>)
                                response -> rxUserService.getUserDetail(active_userid))
                        .flatMap((Function<Response<UserDetail>, Single<Response<ArrayList<UserBrief>>>>)
                                response -> {
                                    userDetailInfo = response.body();
                                    return modeSingle;
                                }
                        )
                        .subscribe(response->{
                            userList = response.body();
                            setListData();
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }

    void setListData() {
        followListAdapter.setActiveUserFollow(userDetailInfo.getFollow());

        followListAdapter.setNewData(userList);
        followListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.followlistRecycler.getParent());
    }
}