package quvesoft.project2.activities;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import quvesoft.project2.Preferences;
import quvesoft.project2.R;
import quvesoft.project2.base.BaseActivity;
import quvesoft.project2.classes.User;
import quvesoft.project2.utils.BasicCallback;
import quvesoft.project2.utils.ChefAuth;
import quvesoft.project2.utils.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

import static quvesoft.project2.Preferences.tempgrid;

public class UserHomeActivity extends BaseActivity {
    @BindView(R.id.userhome_recycler)
    RecyclerView recyclerView;

    private PostListAdapter postListAdapter;
    private ArrayList<PostItem> postItems = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private UserHomeService userHomeService;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_userhome);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.userhome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserID = getIntent().getStringExtra("UserID");

        postListAdapter = new PostListAdapter(Glide.with(this));

        recyclerView.setLayoutManager(new GridLayoutManager(UserHomeActivity.this, 3));
        recyclerView.setAdapter(postListAdapter);
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        postListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (postItems.get(position).getType() == Preferences.HOMEITEM_TYPE_PHOTO)
                    startActivity(new Intent(UserHomeActivity.this, PostActivity.class));
                else if (postItems.get(position).getType() == Preferences.HOMEITEM_TYPE_RECIPE)
                    startActivity(new Intent(UserHomeActivity.this, RecipeActivity.class));
            }
        });

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (ChefAuth.isLogIn(UserHomeActivity.this)) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            userHomeService = RetrofitBuilder.create(UserHomeActivity.this, UserHomeService.class, true, task.getResult().getToken());
                        else
                            userHomeService = RetrofitBuilder.create(UserHomeActivity.this, UserHomeService.class, false);
                        setInfo();
                    });
        } else {
            userHomeService = RetrofitBuilder.create(UserHomeActivity.this, UserHomeService.class, false);
            setInfo();
        }
    }

    private void setInfo() {
        Map<String, String> getBasicOptionMap = new HashMap<>();
        getBasicOptionMap.put("User_ID", UserID);

        try {
            getBasicOptionMap.put("myID", new JSONObject(mSharedPreferences.getString(Preferences.SHAREDPREFERENCE_USERINFO, null)).getString("USER_ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userHomeService.GetBasicInfoCall(getBasicOptionMap)
                .enqueue(new BasicCallback<User>(UserHomeActivity.this) {
                    @Override
                    public void onResponse(Response<User> response) {
                        postListAdapter.setHeaderView(setheaderview(response.body()));
                        postListAdapter.setHeaderAndEmpty(true);

                        for (int i = 0; i < 3; i++) {
                            Random r = new Random();

                            postItems.add(new PostItem(Preferences.HOMEITEM_TYPE_PHOTO, Integer.toString(tempgrid[r.nextInt(6)]), 1));
                            postItems.add(new PostItem(Preferences.HOMEITEM_TYPE_RECIPE, Integer.toString(tempgrid[r.nextInt(6)]), 0));
                            postItems.add(new PostItem(Preferences.HOMEITEM_TYPE_PHOTO, Integer.toString(tempgrid[r.nextInt(6)]), 0));
                            postItems.add(new PostItem(Preferences.HOMEITEM_TYPE_RECIPE, Integer.toString(tempgrid[r.nextInt(6)]), 0));
                            postItems.add(new PostItem(Preferences.HOMEITEM_TYPE_PHOTO, Integer.toString(tempgrid[r.nextInt(6)]), 0));
                            postItems.add(new PostItem(Preferences.HOMEITEM_TYPE_PHOTO, Integer.toString(tempgrid[r.nextInt(6)]), 0));
                        }

                        postListAdapter.setNewData(postItems);
                        postListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                    }

                    @Override
                    public void onFailure() {

                    }
                });
    }

    View setheaderview(User userInfo) {
        View itemView = getLayoutInflater().inflate(R.layout.h_home, (ViewGroup) recyclerView.getParent(), false);

        Glide.with(UserHomeActivity.this)
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
            Intent intent = new Intent(UserHomeActivity.this, MyRecipeActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        });
        itemView.findViewById(R.id.home_followerlayout).setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, FollowerListActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        });
        itemView.findViewById(R.id.home_followinglayout).setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, FollowingListActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        });

        if (mSharedPreferences.getBoolean(Preferences.SHAREDPREFERENCE_AUTOLOGIN, false)) {
            if (userInfo.getFollowingButton() == 0 || userInfo.getFollowingButton() == 1) {
                AppCompatButton followingButton = ((AppCompatButton) itemView.findViewById(R.id.home_button));
                followingButton.setText(userInfo.getFollowingButton() == 1 ? "팔로ing 누르면 언팔" : "팔로우하기");
                followingButton.setVisibility(View.VISIBLE);

                followingButton.setOnClickListener(v -> {

                    int follow = userInfo.getFollowingButton();

                    userInfo.setFollowingButton(userInfo.getFollowingButton() >= 1 ? 0 : 1);

                    followingButton.setText(userInfo.getFollowingButton() == 1 ? "팔로ing 누르면 언팔" : "팔로우하기");

                    userHomeService.FollowCall(UserID, follow)
                            .enqueue(new BasicCallback<JSONObject>(UserHomeActivity.this) {
                                @Override
                                public void onResponse(Response<JSONObject> response) {
                                    try {
                                        userInfo.setFollowingButton(response.body().getInt("follow"));
                                        followingButton.setText(userInfo.getFollowingButton() == 1 ? "팔로ing 누르면 언팔" : "팔로우하기");
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

    private interface UserHomeService {
        @GET("user/info/home.php")
        Call<User> GetBasicInfoCall(@QueryMap Map<String, String> option);

        @FormUrlEncoded
        @POST("user/follow/follow.php")
        Call<JSONObject> FollowCall(@Field("User_ID") String userID, @Field("follow") int follow);
    }

    private class PostItem {
        private int Type;
        private String ImageUrl;
        private int Time;

        private PostItem(int type, String imageUrl, int time) {
            Type = type;
            ImageUrl = imageUrl;
            Time = time;
        }

        private int getType() {
            return Type;
        }

        private String getImageUrl() {
            return ImageUrl;
        }

        private int getTime() {
            return Time;
        }
    }

    private class PostListAdapter extends BaseQuickAdapter<PostItem, BaseViewHolder> {
        private RequestManager requestManager;

        PostListAdapter(RequestManager requestManager) {
            super(R.layout.li_homegrid);
            this.requestManager = requestManager;
        }

        @Override
        protected void convert(BaseViewHolder helper, PostItem item) {
            ViewGroup.LayoutParams lp = helper.itemView.findViewById(R.id.li_homegrid_recipeimg).getLayoutParams();

            lp.width = recyclerView.getMeasuredWidth() / 3;
            lp.height = recyclerView.getMeasuredWidth() / 3;
            helper.itemView.findViewById(R.id.li_homegrid_recipeimg).setLayoutParams(lp);

            requestManager
                    .load(Integer.valueOf(item.getImageUrl()))
                    .apply(RequestOptions.centerCropTransform())
                    .into((AppCompatImageView) helper.getView(R.id.li_homegrid_recipeimg));
            helper.setVisible(R.id.li_homegrid_type, item.getType() == Preferences.HOMEITEM_TYPE_RECIPE);
            helper.setVisible(R.id.li_homegrid_new, item.getTime() == 1);
        }
    }
}