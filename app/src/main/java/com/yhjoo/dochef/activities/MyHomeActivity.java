package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yhjoo.dochef.Preferences;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.User;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.PermissionUtil;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.yhjoo.dochef.Preferences.tempgrid;

public class MyHomeActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;
    @BindView(R.id.myhome_recycler)
    RecyclerView recyclerView;
    AppCompatButton appCompatButton;
    AppCompatImageView userimg;
    private final ArrayList<View> revise_Icons = new ArrayList<>();
    private PostListAdapter postListAdapter;
    private final ArrayList<PostItem> postItems = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private MyHomeService myHomeService;
    private JSONObject userInfoJson;
    private User userInfo;
    private int currentMode = 1;
    private final int mode_Normal = 1;
    private final int mode_Revise = 2;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_myhome);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myhome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        postListAdapter = new PostListAdapter(Glide.with(this));

        try {
            userInfoJson = new JSONObject(mSharedPreferences.getString(Preferences.SHAREDPREFERENCE_USERINFO, null));
            userInfo = new User(userInfoJson);
            postListAdapter.setHeaderView(setheaderview());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new GridLayoutManager(MyHomeActivity.this, 3));
        recyclerView.setAdapter(postListAdapter);
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        postListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (postItems.get(position).getType() == Preferences.HOMEITEM_TYPE_PHOTO)
                startActivity(new Intent(MyHomeActivity.this, PostActivity.class));
            else if (postItems.get(position).getType() == Preferences.HOMEITEM_TYPE_RECIPE)
                startActivity(new Intent(MyHomeActivity.this, RecipeActivity.class));
        });

//        TODO 이거 쳐내고 chefauth에 합치기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (ChefAuth.isLogIn(MyHomeActivity.this)) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            myHomeService = RetrofitBuilder.create(MyHomeActivity.this, MyHomeService.class, true, task.getResult().getToken());
                        else
                            myHomeService = RetrofitBuilder.create(MyHomeActivity.this, MyHomeService.class, false);
                        setInfo();
                    });
        } else {
            myHomeService = RetrofitBuilder.create(MyHomeActivity.this, MyHomeService.class, false);
            setInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRA_RQ_PICKFROMGALLERY)
            if (data != null) {
                Glide.with(this)
                        .load(mImageUri != null ? mImageUri : data.getData())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(userimg);
            }
    }

    @Override
    public void onBackPressed() {
        if (currentMode == mode_Revise) {
            currentMode = mode_Normal;

            appCompatButton.setText("프로필 수정");
            for (int i = 0; i < revise_Icons.size(); i++)
                revise_Icons.get(i).setVisibility(View.GONE);
        } else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION) {
            for (int result : grantResults)
                if (result == PackageManager.PERMISSION_DENIED) {
                    App.getAppInstance().showToast("권한 거부");
                    return;
                }

            mImageUri = Uri.fromFile(new File(getExternalCacheDir(), "filterimage"));

            Intent intent = new Intent(Intent.ACTION_PICK)
                    .setType(MediaStore.Images.Media.CONTENT_TYPE)
                    .putExtra("crop", "true")
                    .putExtra("aspectX", 1)
                    .putExtra("aspectY", 1)
                    .putExtra("scale", true)
                    .putExtra("output", mImageUri);
            startActivityForResult(intent, EXTRA_RQ_PICKFROMGALLERY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setInfo() {
        myHomeService.GetBasicInfoCall(userInfo.getUserID())
                .enqueue(new BasicCallback<User>(MyHomeActivity.this) {
                    @Override
                    public void onResponse(Response<User> response) {
                        userInfo = response.body();

                        if (userInfo != null) {
                            postListAdapter.setHeaderView(setheaderview());
                            postListAdapter.setHeaderAndEmpty(true);

                            try {
                                userInfoJson.put("NICKNAME", userInfo.getNickname());
                                userInfoJson.put("PROFILE_IMAGE", userInfo.getUserImg());
                                userInfoJson.put("INTRODUCTION", userInfo.getProfileText());

                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString(Preferences.SHAREDPREFERENCE_USERINFO, userInfoJson.toString());
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

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
                });
    }

    View setheaderview() {
        View itemView = getLayoutInflater().inflate(R.layout.h_home, (ViewGroup) recyclerView.getParent(), false);

        userimg = (AppCompatImageView) itemView.findViewById(R.id.home_userimg);
        Glide.with(MyHomeActivity.this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + userInfo.getUserImg())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).circleCrop())
                .into(userimg);

        ((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).setText(userInfo.getNickname());
        ((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).setText(userInfo.getProfileText());
        ((AppCompatTextView) itemView.findViewById(R.id.home_recipecount)).setText(String.valueOf(userInfo.getRecipeCount()));
        ((AppCompatTextView) itemView.findViewById(R.id.home_followercount)).setText(String.valueOf(userInfo.getFollowerCount()));
        ((AppCompatTextView) itemView.findViewById(R.id.home_followingcount)).setText(String.valueOf(userInfo.getFollowingCount()));
        ((AppCompatButton) itemView.findViewById(R.id.home_button)).setText("프로필 수정");
        revise_Icons.add(itemView.findViewById(R.id.home_userimg_revise));
        revise_Icons.add(itemView.findViewById(R.id.home_nickname_revise));
        revise_Icons.add(itemView.findViewById(R.id.home_profiletext_revise));
        itemView.findViewById(R.id.home_button).setVisibility(View.VISIBLE);

        itemView.findViewById(R.id.home_userimglayout).setOnClickListener((v -> {
            if (currentMode == mode_Revise) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(new String[]{"이미지 변경", "삭제"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            final String[] permissions = {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            };

                            if (PermissionUtil.checkPermission(MyHomeActivity.this, permissions)) {
                                mImageUri = Uri.fromFile(new File(getExternalCacheDir(), "filterimage"));

                                Intent intent = new Intent(Intent.ACTION_PICK)
                                        .setType(MediaStore.Images.Media.CONTENT_TYPE)
                                        .putExtra("crop", "true")
                                        .putExtra("aspectX", 1)
                                        .putExtra("aspectY", 1)
                                        .putExtra("scale", true)
                                        .putExtra("output", mImageUri);
                                startActivityForResult(intent, EXTRA_RQ_PICKFROMGALLERY);
                            } else
                                ActivityCompat.requestPermissions(MyHomeActivity.this, permissions, CODE_PERMISSION);
                        } else if (which == 1)
                            App.getAppInstance().showToast("삭제");
                        dialog.dismiss();
                    }
                }).show();
            }
        }));
        itemView.findViewById(R.id.home_nicknamelayout).setOnClickListener((v -> {
            if (currentMode == mode_Revise) {
                AppCompatEditText editText = (AppCompatEditText) getLayoutInflater().inflate(R.layout.v_home_nickname, null);
                editText.setHint(((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("닉네임 변경")
                        .setView(editText)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).setText(editText.getText().toString());
                                App.getAppInstance().showToast("변경되었습니다.");
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }));
        itemView.findViewById(R.id.home_profiletextlayout).setOnClickListener((v -> {
            if (currentMode == mode_Revise) {
                AppCompatEditText editText = (AppCompatEditText) getLayoutInflater().inflate(R.layout.v_home_profile, null);
                editText.setHint(((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("프로필 변경")
                        .setView(editText)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).setText(editText.getText().toString());
                                App.getAppInstance().showToast("변경되었습니다.");
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }));

        itemView.findViewById(R.id.home_recipelayout).setOnClickListener((v -> {
            Intent intent = new Intent(MyHomeActivity.this, MyRecipeActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        }));
        itemView.findViewById(R.id.home_followerlayout).setOnClickListener((v -> {
            Intent intent = new Intent(MyHomeActivity.this, FollowerListActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        }));
        itemView.findViewById(R.id.home_followinglayout).setOnClickListener((v -> {
            Intent intent = new Intent(MyHomeActivity.this, FollowingListActivity.class);
            intent.putExtra("UserID", userInfo.getUserID());
            startActivity(intent);
        }));

        appCompatButton = (AppCompatButton) itemView.findViewById(R.id.home_button);
        appCompatButton.setOnClickListener(v -> {
            if (currentMode == mode_Normal) {
                currentMode = mode_Revise;
                ((AppCompatButton) v).setText("변경 완료");
                for (int i = 0; i < revise_Icons.size(); i++)
                    revise_Icons.get(i).setVisibility(View.VISIBLE);

            } else if (currentMode == mode_Revise) {
                currentMode = mode_Normal;
                ((AppCompatButton) v).setText("프로필 수정");
                for (int i = 0; i < revise_Icons.size(); i++)
                    revise_Icons.get(i).setVisibility(View.GONE);
            }
        });

        return itemView;
    }

    private interface MyHomeService {
        @GET("user/info/home.php")
        Call<User> GetBasicInfoCall(@Query("User_ID") String id);
    }

    private class PostItem {
        private final int Type;
        private final String ImageUrl;
        private final int Time;

        PostItem(int type, String imageUrl, int time) {
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

        public int getTime() {
            return Time;
        }
    }

    private class PostListAdapter extends BaseQuickAdapter<PostItem, BaseViewHolder> {
        private final RequestManager requestManager;

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


            requestManager.load(Integer.valueOf(item.getImageUrl()))
                    .into((AppCompatImageView) helper.getView(R.id.li_homegrid_recipeimg));

            helper.setVisible(R.id.li_homegrid_type, item.getType() == Preferences.HOMEITEM_TYPE_RECIPE);
            helper.setVisible(R.id.li_homegrid_new, item.getTime() == 1);
        }
    }
}
