package com.yhjoo.dochef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.PostThumbnail;
import com.yhjoo.dochef.classes.User;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class HomeUserActivity extends BaseActivity {
    @BindView(R.id.userhome_recycler)
    RecyclerView recyclerView;

    private ArrayList<PostThumbnail> PostThumbnails = new ArrayList<>();

    private PostListAdapter postListAdapter;
    private SharedPreferences mSharedPreferences;
    private RetrofitServices.UserHomeService userHomeService;
    private String UserID;

    /*
        TODO
        이거 합쳐질거임
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_home_user);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.userhome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserID = getIntent().getStringExtra("UserID");

        postListAdapter = new PostListAdapter(Glide.with(this));

        recyclerView.setLayoutManager(new GridLayoutManager(HomeUserActivity.this, 3));
        recyclerView.setAdapter(postListAdapter);
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        postListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (PostThumbnails.get(position).getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_PHOTO))
                startActivity(new Intent(HomeUserActivity.this, PostDetailActivity.class));
            else if (PostThumbnails.get(position).getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_RECIPE))
                startActivity(new Intent(HomeUserActivity.this, RecipeDetailActivity.class));
        });

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        TODO 이거 쳐내고 chefauth에 합치기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (ChefAuth.isLogIn(HomeUserActivity.this)) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            userHomeService = RetrofitBuilder.create(HomeUserActivity.this, RetrofitServices.UserHomeService.class, true, task.getResult().getToken());
                        else
                            userHomeService = RetrofitBuilder.create(HomeUserActivity.this, RetrofitServices.UserHomeService.class, false);
                        setInfo();
                    });
        } else {
            userHomeService = RetrofitBuilder.create(HomeUserActivity.this, RetrofitServices.UserHomeService.class, false);
            setInfo();
        }
    }

    private void setInfo() {
        Map<String, String> getBasicOptionMap = new HashMap<>();
        getBasicOptionMap.put("User_ID", UserID);

        try {
            getBasicOptionMap.put("myID", new JSONObject(mSharedPreferences.getString(getString(R.string.SHAREDPREFERENCE_USERINFO), null)).getString("USER_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userHomeService.GetBasicInfoCall(getBasicOptionMap)
                .enqueue(new BasicCallback<User>(HomeUserActivity.this) {
                    @Override
                    public void onResponse(Response<User> response) {
                        postListAdapter.setHeaderView(setheaderview(response.body()));
                        postListAdapter.setHeaderAndEmpty(true);

                        PostThumbnails = DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_GRID));

                        postListAdapter.setNewData(PostThumbnails);
                        postListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                    }
                });
    }

    View setheaderview(User userInfo) {
        View itemView = getLayoutInflater().inflate(R.layout.h_home, (ViewGroup) recyclerView.getParent(), false);

        Glide.with(HomeUserActivity.this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + userInfo.getUserImg())
                .apply(RequestOptions.circleCropTransform())
                .into((AppCompatImageView) itemView.findViewById(R.id.home_userimg));

        ((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).setText(userInfo.getNickname());
        getSupportActionBar().setTitle(userInfo.getNickname());
        ((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).setText(userInfo.getProfileText());
        ((AppCompatTextView) itemView.findViewById(R.id.home_recipecount)).setText(String.valueOf(userInfo.getRecipeCount()));
        ((AppCompatTextView) itemView.findViewById(R.id.home_followercount)).setText(String.valueOf(userInfo.getFollowerCount()));
        ((AppCompatTextView) itemView.findViewById(R.id.home_followingcount)).setText(String.valueOf(userInfo.getFollowingCount()));

        itemView.findViewById(R.id.home_recipelayout).setOnClickListener(v -> {
            Intent intent = new Intent(HomeUserActivity.this, RecipeListActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        });
        itemView.findViewById(R.id.home_followerlayout).setOnClickListener(v -> {
            Intent intent = new Intent(HomeUserActivity.this, FollowListActivity.class);
            intent.putExtra("mode", FollowListActivity.MODE.FOLLOWER);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        });
        itemView.findViewById(R.id.home_followinglayout).setOnClickListener(v -> {
            Intent intent = new Intent(HomeUserActivity.this, FollowListActivity.class);
            intent.putExtra("mode", FollowListActivity.MODE.FOLLOWING);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        });

        if (mSharedPreferences.getBoolean(getString(R.string.SHAREDPREFERENCE_AUTOLOGIN), false)) {
            if (userInfo.getIs_following() == 0 || userInfo.getIs_following() == 1) {
                AppCompatButton followingButton = ((AppCompatButton) itemView.findViewById(R.id.home_button));
                followingButton.setText(userInfo.getIs_following() == 1 ? "팔로ing 누르면 언팔" : "팔로우하기");
                followingButton.setVisibility(View.VISIBLE);

                followingButton.setOnClickListener(v -> {

                    int follow = userInfo.getIs_following();

                    userInfo.setIs_following(userInfo.getIs_following() >= 1 ? 0 : 1);

                    followingButton.setText(userInfo.getIs_following() == 1 ? "팔로ing 누르면 언팔" : "팔로우하기");

                    userHomeService.FollowCall(UserID, follow)
                            .enqueue(new BasicCallback<JSONObject>(HomeUserActivity.this) {
                                @Override
                                public void onResponse(Response<JSONObject> response) {
                                    try {
                                        userInfo.setIs_following(response.body().getInt("follow"));
                                        followingButton.setText(userInfo.getIs_following() == 1 ? "팔로ing 누르면 언팔" : "팔로우하기");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                });
            }
        }

        return itemView;
    }


    private class PostListAdapter extends BaseQuickAdapter<PostThumbnail, BaseViewHolder> {
        private final RequestManager requestManager;

        PostListAdapter(RequestManager requestManager) {
            super(R.layout.li_homegrid);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, PostThumbnail item) {
            ViewGroup.LayoutParams lp = helper.itemView.findViewById(R.id.li_homegrid_recipeimg).getLayoutParams();

            lp.width = recyclerView.getMeasuredWidth() / 3;
            lp.height = recyclerView.getMeasuredWidth() / 3;
            helper.itemView.findViewById(R.id.li_homegrid_recipeimg).setLayoutParams(lp);

            requestManager
                    .load(Integer.valueOf(item.getImageUrl()))
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_homegrid_recipeimg));
            helper.setVisible(R.id.li_homegrid_type, item.getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_RECIPE));
            helper.setVisible(R.id.li_homegrid_new, item.getIsNew() == 1);
        }
    }
}