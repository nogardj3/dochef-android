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
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.classes.PostThumbnail;
import com.yhjoo.dochef.classes.UserDetail;
import com.yhjoo.dochef.interfaces.RetrofitServices;
import com.yhjoo.dochef.utils.BasicCallback;
import com.yhjoo.dochef.utils.ChefAuth;
import com.yhjoo.dochef.utils.DummyMaker;
import com.yhjoo.dochef.utils.PermissionUtil;
import com.yhjoo.dochef.utils.RetrofitBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;
    private final ArrayList<View> revise_Icons = new ArrayList<>();
    private final ArrayList<PostThumbnail> postItems = new ArrayList<>();
    @BindView(R.id.home_recycler)
    RecyclerView recyclerView;
    AppCompatButton appCompatButton;
    AppCompatImageView userimg;
    OPERATION currentOperation = OPERATION.VIEW;
    private PostListAdapter postListAdapter;
    private SharedPreferences mSharedPreferences;
    private RetrofitServices.MyHomeService myHomeService;
    private JSONObject userInfoJson;
    private UserDetail userDetailInfo;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_home);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        postListAdapter = new PostListAdapter(Glide.with(this));

        try {
            userInfoJson = new JSONObject(mSharedPreferences.getString(getString(R.string.SHAREDPREFERENCE_USERINFO), null));
            userDetailInfo = new UserDetail(userInfoJson);
            postListAdapter.setHeaderView(setheaderview());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 3));
        recyclerView.setAdapter(postListAdapter);
        postListAdapter.setEmptyView(R.layout.rv_loading, (ViewGroup) recyclerView.getParent());
        postListAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (postItems.get(position).getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_PHOTO))
                startActivity(new Intent(HomeActivity.this, PostDetailActivity.class));
            else if (postItems.get(position).getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_RECIPE))
                startActivity(new Intent(HomeActivity.this, RecipeDetailActivity.class));
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (ChefAuth.isLogIn(HomeActivity.this)) {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            myHomeService = RetrofitBuilder.create(HomeActivity.this, RetrofitServices.MyHomeService.class, true, task.getResult().getToken());
                        else
                            myHomeService = RetrofitBuilder.create(HomeActivity.this, RetrofitServices.MyHomeService.class, false);
                        setInfo();
                    });
        } else {
            myHomeService = RetrofitBuilder.create(HomeActivity.this, RetrofitServices.MyHomeService.class, false);
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

    /*
        TODO
        1. userHome과 합침 - MODE, OPERATION 두개로 구분 - 많이 다르면 Fragment로 가르기
        2. chefauth에 firebaseuser 기능으로 합치기
        3. retrofit 구현
    */

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

    private void setInfo() {
        myHomeService.GetBasicInfoCall(userDetailInfo.getUserID())
                .enqueue(new BasicCallback<UserDetail>(HomeActivity.this) {
                    @Override
                    public void onResponse(Response<UserDetail> response) {
                        userDetailInfo = response.body();

                        if (userDetailInfo != null) {
                            postListAdapter.setHeaderView(setheaderview());
                            postListAdapter.setHeaderAndEmpty(true);

                            try {
                                userInfoJson.put("NICKNAME", userDetailInfo.getNickname());
                                userInfoJson.put("PROFILE_IMAGE", userDetailInfo.getUserImg());
                                userInfoJson.put("INTRODUCTION", userDetailInfo.getProfileText());

                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString(getString(R.string.SHAREDPREFERENCE_USERINFO), userInfoJson.toString());
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        postListAdapter.setNewData(DummyMaker.make(getResources(), getResources().getInteger(R.integer.DUMMY_TYPE_GRID)));
                        postListAdapter.setEmptyView(R.layout.rv_empty, (ViewGroup) recyclerView.getParent());
                    }
                });
    }

    View setheaderview() {
        View itemView = getLayoutInflater().inflate(R.layout.h_home, (ViewGroup) recyclerView.getParent(), false);

        userimg = (AppCompatImageView) itemView.findViewById(R.id.home_userimg);
        Glide.with(HomeActivity.this)
                .load("https://s3.ap-northeast-2.amazonaws.com/quvechefbucket/profile/" + userDetailInfo.getUserImg())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).circleCrop())
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
            Intent intent = new Intent(HomeActivity.this, RecipeListActivity.class);
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

    enum MODE {MY, USER}

    enum OPERATION {VIEW, REVISE}

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


            requestManager.load(Integer.valueOf(item.getImageUrl()))
                    .into((AppCompatImageView) helper.getView(R.id.li_homegrid_recipeimg));

            helper.setVisible(R.id.li_homegrid_type, item.getThumbnail_type() == getResources().getInteger(R.integer.HOMEITEM_TYPE_RECIPE));
            helper.setVisible(R.id.li_homegrid_new, item.getIsNew() == 1);
        }
    }
}
