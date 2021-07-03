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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.UserList;
import com.yhjoo.dochef.utils.BasicCallback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.yhjoo.dochef.Preferences.tempprofile;

public class FollowingListActivity extends BaseActivity {
    @BindView(R.id.followinglist_recycler)
    RecyclerView recyclerView;

    private UserListAdapter userListAdapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_followinglist);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.followinglist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = getIntent().getStringExtra("UserID");

        userListAdapter = new UserListAdapter(Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userListAdapter);

        userListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        userListAdapter.setOnItemClickListener((adapter, view, position) -> {
                    Intent intent = new Intent(FollowingListActivity.this, UserHomeActivity.class);
                    intent.putExtra("UserID", ((UserList) adapter.getData().get(position)).getUserID());
                    startActivity(intent);
                }
        );

        FollowingService followingService = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(FollowingService.class);

        followingService.GetFollowingCall(userID, 0)
                .enqueue(new BasicCallback<List<UserList>>(FollowingListActivity.this) {
                    @Override
                    public void onResponse(Response<List<UserList>> response) {
                        userListAdapter.setNewData(response.body());
                        userListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());

                        ArrayList<UserList> aa = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            Random r = new Random();

                            aa.add(new UserList("aa", Integer.toString(tempprofile[r.nextInt(6)]), "유저" + i));
                            aa.add(new UserList("aa", Integer.toString(tempprofile[r.nextInt(6)]), "유저" + i));
                            aa.add(new UserList("aa", Integer.toString(tempprofile[r.nextInt(6)]), "유저" + i));
                            aa.add(new UserList("aa", Integer.toString(tempprofile[r.nextInt(6)]), "유저" + i));
                            aa.add(new UserList("aa", Integer.toString(tempprofile[r.nextInt(6)]), "유저" + i));
                            aa.add(new UserList("aa", Integer.toString(tempprofile[r.nextInt(6)]), "유저" + i));
                        }

                        userListAdapter.setNewData(aa);
                    }
                });
    }

    private interface FollowingService {
        @GET("user/follow/followinglist.php")
        Call<List<UserList>> GetFollowingCall(@Query("UserID") String userID, @Query("last") int last);
    }

    private class UserListAdapter extends BaseQuickAdapter<UserList, BaseViewHolder> {
        private final RequestManager requestManager;

        UserListAdapter(RequestManager requestManager) {
            super(R.layout.li_following);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, UserList item) {
            requestManager
//                    .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + item.getUserImg())
                    .load(Integer.valueOf(item.getUserImg()))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).circleCrop())
                    .into((AppCompatImageView) helper.getView(R.id.li_following_userimg));

            helper.setText(R.id.li_following_nickname, item.getNickname());
        }
    }
}