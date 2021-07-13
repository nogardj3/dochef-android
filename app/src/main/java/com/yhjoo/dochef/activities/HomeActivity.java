package com.yhjoo.dochef.activities;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.RecipeGridAdapter;
import com.yhjoo.dochef.databinding.AHomeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.RecipeBrief;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;

    public enum MODE {MY, USER}

    enum OPERATION {VIEW, REVISE}

    AHomeBinding binding;
    RetrofitServices.UserService userService;
    RetrofitServices.RecipeService recipeService;
    SharedPreferences mSharedPreferences;
    RecipeGridAdapter recipeGridAdapter;

    ArrayList<View> revise_Icons = new ArrayList<>();
    ArrayList<RecipeBrief> recipeList = new ArrayList<>();

    AppCompatButton appCompatButton;
    AppCompatImageView userimg;
    JSONObject userInfoJson;
    UserDetail userDetailInfo;
    Uri mImageUri;

    String userID = "";
    MODE currentMode = MODE.MY;
    OPERATION currentOperation = OPERATION.VIEW;

    /*
        TODO
        1. userHome과 합침 - MODE, OPERATION 두개로 구분
        2. UserService - headerview - recycler로 말고 밖으로 빼기
        3. RecipeService - recipe grid 꾸미기
        4. recipe 가로로, post 세로로 각각 따로
        4. 서버 작업 / retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.homeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentMode = (MODE) getIntent().getSerializableExtra("MODE");


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            userInfoJson = new JSONObject(mSharedPreferences.getString(getString(R.string.SP_USERINFO), null));
            userDetailInfo = new UserDetail(userInfoJson);
            userID = userInfoJson.getString("user_id");
            getUserDetailInfo(userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recipeGridAdapter = new RecipeGridAdapter(binding.homeRecycler);
        recipeGridAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) binding.homeRecycler.getParent());
        recipeGridAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (recipeList.get(position).getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_PHOTO))
                startActivity(new Intent(HomeActivity.this, PostDetailActivity.class));
            else if (recipeList.get(position).getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_RECIPE))
                startActivity(new Intent(HomeActivity.this, RecipeDetailActivity.class));
        });

//        if (App.isServerAlive())
//            getListFromServer();
//        else
//            getListFromLocal();

        binding.homeRecycler.setLayoutManager(new GridLayoutManager(HomeActivity.this, 3));
        binding.homeRecycler.setAdapter(recipeGridAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRA_RQ_PICKFROMGALLERY)
            if (data != null) {
                Glide.with(this)
                        .load(mImageUri != null ? mImageUri : data.getData())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_default_profile))
                        .into(userimg);
            }
    }

    @Override
    public void onBackPressed() {
        if (currentOperation == OPERATION.REVISE) {
            currentOperation = OPERATION.VIEW;

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

    void getUserDetailInfo(String userID) {
        // 디테일 가져와서 헤더뷰 붙이기
        recipeGridAdapter.setHeaderView(setheaderview());
    }

    void setInfo() {
//        myHomeService.GetBasicInfoCall(userDetailInfo.getUserID())
//                .enqueue(new BasicCallback<UserDetail>(HomeActivity.this) {
//                    @Override
//                    public void onResponse(Response<UserDetail> response) {
//                        userDetailInfo = response.body();
//
//                        if (userDetailInfo != null) {
//                            recipeGridAdapter.setHeaderView(setheaderview());
//                            recipeGridAdapter.setHeaderAndEmpty(true);
//
//                            try {
//                                userInfoJson.put("NICKNAME", userDetailInfo.getNickname());
//                                userInfoJson.put("PROFILE_IMAGE", userDetailInfo.getUserImg());
//                                userInfoJson.put("INTRODUCTION", userDetailInfo.getProfileText());
//
//                                SharedPreferences.Editor editor = mSharedPreferences.edit();
//                                editor.putString(getString(R.string.SP_USERINFO), userInfoJson.toString());
//                                editor.apply();
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        recipeGridAdapter.setNewData(DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_GRID)));
//                        recipeGridAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.homeRecycler.getParent());
//                    }
//                });
    }

    View setheaderview() {
        View itemView = getLayoutInflater().inflate(R.layout.h_home, (ViewGroup) binding.homeRecycler.getParent(), false);

        userimg = (AppCompatImageView) itemView.findViewById(R.id.home_userimg);
        Glide.with(HomeActivity.this)
                .load(getString(R.string.storage_image_url_profile) + userDetailInfo.getUserImg())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_default_profile))
                .into(userimg);

        ((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).setText(userDetailInfo.getNickname());
        ((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).setText(userDetailInfo.getProfileText());
        ((AppCompatTextView) itemView.findViewById(R.id.home_recipecount)).setText(String.valueOf(userDetailInfo.getRecipeCount()));
        ((AppCompatTextView) itemView.findViewById(R.id.home_followercount)).setText(String.valueOf(userDetailInfo.getFollowerCount()));
        ((AppCompatTextView) itemView.findViewById(R.id.home_followingcount)).setText(String.valueOf(userDetailInfo.getFollowingCount()));
        ((AppCompatButton) itemView.findViewById(R.id.home_button)).setText("프로필 수정");
        revise_Icons.add(itemView.findViewById(R.id.home_userimg_revise));
        revise_Icons.add(itemView.findViewById(R.id.home_nickname_revise));
        revise_Icons.add(itemView.findViewById(R.id.home_profiletext_revise));
        itemView.findViewById(R.id.home_button).setVisibility(View.VISIBLE);

        itemView.findViewById(R.id.home_userimg_revise).setOnClickListener((v -> {
            if (currentOperation == OPERATION.REVISE) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(new String[]{"이미지 변경", "삭제"}, (dialog, which) -> {
                    if (which == 0) {
                        final String[] permissions = {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        };

                        if (PermissionUtil.checkPermission(HomeActivity.this, permissions)) {
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
                            ActivityCompat.requestPermissions(HomeActivity.this, permissions, CODE_PERMISSION);
                    } else if (which == 1)
                        App.getAppInstance().showToast("삭제");
                    dialog.dismiss();
                }).show();
            }
        }));
        itemView.findViewById(R.id.home_nickname_revise).setOnClickListener((v -> {
            if (currentOperation == OPERATION.REVISE) {
                AppCompatEditText editText = (AppCompatEditText) getLayoutInflater().inflate(R.layout.v_home_nickname, null);
                editText.setHint(((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("닉네임 변경")
                        .setView(editText)
                        .setPositiveButton("확인", (dialog, which) -> {
                            ((AppCompatTextView) itemView.findViewById(R.id.home_nickname)).setText(editText.getText().toString());
                            App.getAppInstance().showToast("변경되었습니다.");
                            dialog.dismiss();
                        })
                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss()).show();
            }
        }));
        itemView.findViewById(R.id.home_profiletext_revise).setOnClickListener((v -> {
            if (currentOperation == OPERATION.REVISE) {
                AppCompatEditText editText = (AppCompatEditText) getLayoutInflater().inflate(R.layout.v_home_profile, null);
                editText.setHint(((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("프로필 변경")
                        .setView(editText)
                        .setPositiveButton("확인", (dialog, which) -> {
                            ((AppCompatTextView) itemView.findViewById(R.id.home_profiletext)).setText(editText.getText().toString());
                            App.getAppInstance().showToast("변경되었습니다.");
                            dialog.dismiss();
                        })
                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss()).show();
            }
        }));

        itemView.findViewById(R.id.home_recipelayout).setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, RecipeMyListActivity.class);
            intent.putExtra("UserID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
        itemView.findViewById(R.id.home_followerlayout).setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, FollowListActivity.class);
            intent.putExtra("mode", FollowListActivity.MODE.FOLLOWER);
            intent.putExtra("UserID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
        itemView.findViewById(R.id.home_followinglayout).setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, FollowListActivity.class);
            intent.putExtra("mode", FollowListActivity.MODE.FOLLOWING);
            intent.putExtra("UserID", userDetailInfo.getUserID());
            startActivity(intent);
        }));

        appCompatButton = (AppCompatButton) itemView.findViewById(R.id.home_button);
        appCompatButton.setOnClickListener(v -> {
            if (currentOperation == OPERATION.VIEW) {
                currentOperation = OPERATION.REVISE;
                ((AppCompatButton) v).setText("변경 완료");
                for (int i = 0; i < revise_Icons.size(); i++)
                    revise_Icons.get(i).setVisibility(View.VISIBLE);

            } else if (currentOperation == OPERATION.REVISE) {
                currentOperation = OPERATION.VIEW;
                ((AppCompatButton) v).setText("프로필 수정");
                for (int i = 0; i < revise_Icons.size(); i++)
                    revise_Icons.get(i).setVisibility(View.GONE);
            }
        });

        return itemView;
    }

    void getRecipeListFromServer(String userID) {

    }

    void getRecipeListFromLocal(String userID) {

    }
}
