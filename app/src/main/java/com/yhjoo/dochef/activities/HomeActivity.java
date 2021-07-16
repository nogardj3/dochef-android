package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.PostListAdapter;
import com.yhjoo.dochef.adapter.RecipeHorizontalAdapter;
import com.yhjoo.dochef.databinding.AHomeBinding;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.RetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;

    enum MODE {MY, USER}

    enum OPERATION {VIEW, REVISE}

    AHomeBinding binding;
    RetrofitServices.UserService userService;
    RetrofitServices.RecipeService recipeService;
    RetrofitServices.PostService postService;
    RecipeHorizontalAdapter recipeHorizontalAdapter;
    PostListAdapter postListAdapter;

    ArrayList<Recipe> recipeList = new ArrayList<>();
    ArrayList<Post> postList = new ArrayList<>();
    UserDetail userDetailInfo;
    Uri mImageUri;

    MODE currentMode;
    OPERATION currentOperation = OPERATION.VIEW;
    String userID;

    /*
        TODO
        revise 구현 - 각각 말고 완료 누를 때 적용하기
        recipe, post emptyview
        user, my enum 없애기
        follow 확인
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userService = RetrofitBuilder.create(this, RetrofitServices.UserService.class);
        recipeService = RetrofitBuilder.create(this, RetrofitServices.RecipeService.class);
        postService = RetrofitBuilder.create(this, RetrofitServices.PostService.class);

        userID = Utils.getUserBrief(this).getUserID();
        if(getIntent().getStringExtra("userID").equals("")
                || getIntent().getStringExtra("userID").equals(userID))
            currentMode = MODE.MY;
        else
            currentMode = MODE.USER;

        recipeHorizontalAdapter = new RecipeHorizontalAdapter();
        recipeHorizontalAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(HomeActivity.this, RecipeDetailActivity.class)
                    .putExtra("recipeID", recipeList.get(position).getRecipeID());
            startActivity(intent);
        });
        binding.homeRecipeRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.homeRecipeRecycler.setAdapter(recipeHorizontalAdapter);

        postListAdapter = new PostListAdapter();
        postListAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(HomeActivity.this, PostDetailActivity.class)
                    .putExtra("postID", postList.get(position).getPostID());
            startActivity(intent);
        });
        binding.homePostRecycler.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.homePostRecycler.setAdapter(postListAdapter);

        if (App.isServerAlive()) {
            getUserDetailInfo(userID);
            getRecipeList(userID);
            getPostList(userID);
        } else {
            userDetailInfo = DataGenerator.make(getResources(), R.integer.DATA_TYPE_RECIPE_DETAIL);
            recipeList = DataGenerator.make(getResources(), R.integer.DATE_TYPE_RECIPE);
            postList = DataGenerator.make(getResources(), R.integer.DATA_TYPE_POST);

            setUserInfo();
            recipeHorizontalAdapter.setNewData(recipeList);
            postListAdapter.setNewData(postList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRA_RQ_PICKFROMGALLERY)
            if (data != null) {
                mImageUri = data.getData();
                Glide.with(this)
                        .load(mImageUri)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(binding.homeUserimg);
            }
    }

    @Override
    public void onBackPressed() {
        if (currentOperation == OPERATION.REVISE) {
            currentOperation = OPERATION.VIEW;

            binding.homeReviseBtn.setText("프로필 수정");
            binding.homeRevisegroup.setVisibility(View.GONE);
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
        userService.getUserDetail(userID)
                .enqueue(new BasicCallback<UserDetail>(this) {
                    @Override
                    public void onResponse(Call<UserDetail> call, Response<UserDetail> response) {
                        super.onResponse(call, response);
                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            userDetailInfo = response.body();
                            setUserInfo();
                        }
                    }
                });
    }

    void getRecipeList(String userID) {
        recipeService.getRecipeByUserID(userID, "latest")
                .enqueue(new BasicCallback<ArrayList<Recipe>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            recipeList = response.body();
                            recipeHorizontalAdapter.setNewData(recipeList);
                            recipeHorizontalAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.homeRecipeRecycler.getParent());
                        }
                    }
                });
    }

    void getPostList(String userID) {
        postService.getPostListByUserID(userID)
                .enqueue(new BasicCallback<ArrayList<Post>>(this) {
                    @Override
                    public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                        super.onResponse(call, response);

                        if (response.code() == 403)
                            App.getAppInstance().showToast("뭔가에러");
                        else {
                            postList = response.body();
                            postListAdapter.setNewData(postList);
                            postListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) binding.homePostRecycler.getParent());
                        }
                    }
                });
    }

    void setUserInfo() {
        if (App.isServerAlive()) {
            if (!userDetailInfo.getUserImg().equals("default"))
                Glide.with(this)
                        .load(getString(R.string.storage_image_url_profile) + userDetailInfo.getUserImg())
                        .circleCrop()
                        .into(binding.homeUserimg);
        } else {
            Glide.with(this)
                    .load(Integer.parseInt(userDetailInfo.getUserImg()))
                    .circleCrop()
                    .into(binding.homeUserimg);
        }

        binding.homeNickname.setText(userDetailInfo.getNickname());
        binding.homeProfiletext.setText(userDetailInfo.getProfileText());
        binding.homeRecipecount.setText(Integer.toString(userDetailInfo.getRecipeCount()));
        binding.homeFollowercount.setText(Integer.toString(userDetailInfo.getFollowerCount()));
        binding.homeFollowingcount.setText(Integer.toString(userDetailInfo.getFollowingCount()));

        if (currentMode == MODE.MY)
            binding.homeReviseBtn.setVisibility(View.VISIBLE);
        else
            binding.homeFollowBtn.setVisibility(View.VISIBLE);

        binding.homeUserimgRevise.setOnClickListener(this::reviseProfileImage);
        binding.homeNicknameRevise.setOnClickListener(this::reviseNickname);
        binding.homeUserimgRevise.setOnClickListener(this::reviseContents);
        binding.homeRecipecount.setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, RecipeMyListActivity.class)
                    .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
        binding.homeFollowercount.setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, FollowListActivity.class)
                    .putExtra("MODE", FollowListActivity.MODE.FOLLOWER)
                    .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
        binding.homeFollowingcount.setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, FollowListActivity.class)
                .putExtra("MODE", FollowListActivity.MODE.FOLLOWING)
                .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        }));

        binding.homeReviseBtn.setOnClickListener(this::changeOperation);
    }

    void changeOperation(View v) {
        if (currentOperation == OPERATION.VIEW) {
            currentOperation = OPERATION.REVISE;
            binding.homeReviseBtn.setText("변경 완료");
            binding.homeRevisegroup.setVisibility(View.VISIBLE);
        } else if (currentOperation == OPERATION.REVISE) {
            currentOperation = OPERATION.VIEW;
            binding.homeRevisegroup.setVisibility(View.GONE);
        }
    }

    void reviseProfileImage(View v) {
        if (currentOperation == OPERATION.REVISE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setItems(new String[]{"이미지 변경", "삭제"}, (dialog, which) -> {
                if (which == 0) {
                    final String[] permissions = {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    };

                    if (Utils.checkPermission(HomeActivity.this, permissions)) {
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
                } else if (which == 1){
                    mImageUri = null;
                    Glide.with(this)
                            .load("")
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true))
                            .into(binding.homeUserimg);
                }
                dialog.dismiss();
            }).show();
        }
    }

    void reviseNickname(View v) {
        if (currentOperation == OPERATION.REVISE) {
            AppCompatEditText editText = (AppCompatEditText) getLayoutInflater().inflate(R.layout.v_home_nickname, null);
            editText.setHint(binding.homeNickname.getText());
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("닉네임 변경")
                    .setView(editText)
                    .setPositiveButton("확인", (dialog, which) -> {
                        binding.homeNickname.setText(editText.getText().toString());
                        App.getAppInstance().showToast("변경되었습니다.");
                        dialog.dismiss();
                    })
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss()).show();
        }
    }

    void reviseContents(View v) {
        if (currentOperation == OPERATION.REVISE) {
            AppCompatEditText editText = (AppCompatEditText) getLayoutInflater().inflate(R.layout.v_home_profile, null);
            editText.setHint(binding.homeProfiletext.getText());
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("프로필 변경")
                    .setView(editText)
                    .setPositiveButton("확인", (dialog, which) -> {
                        binding.homeProfiletext.setText(editText.getText().toString());
                        App.getAppInstance().showToast("변경되었습니다.");
                        dialog.dismiss();
                    })
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss()).show();
        }
    }
}
