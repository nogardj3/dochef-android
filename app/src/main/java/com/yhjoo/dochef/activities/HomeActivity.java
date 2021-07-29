package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.adapter.PostListAdapter;
import com.yhjoo.dochef.adapter.RecipeHorizontalHomeAdapter;
import com.yhjoo.dochef.databinding.AHomeBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.UserDetail;
import com.yhjoo.dochef.utils.DataGenerator;
import com.yhjoo.dochef.utils.GlideApp;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int IMG_WIDTH = 360;
    private final int IMG_HEIGHT = 360;

    private enum MODE {OWNER, OTHERS}

    private enum OPERATION {VIEW, REVISE}

    AHomeBinding binding;
    StorageReference storageReference;
    RxRetrofitServices.AccountService accountService;
    RxRetrofitServices.UserService userService;
    RxRetrofitServices.RecipeService recipeService;
    RxRetrofitServices.PostService postService;
    RecipeHorizontalHomeAdapter recipeHorizontalHomeAdapter;
    PostListAdapter postListAdapter;

    MenuItem reviseMenu;
    MenuItem okMenu;

    ArrayList<Recipe> recipeList;
    ArrayList<Post> postList;
    UserDetail userDetailInfo;

    MODE currentMode;
    OPERATION currentOperation = OPERATION.VIEW;
    Uri mImageUri;
    String image_url;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference();

        accountService = RxRetrofitBuilder.create(this, RxRetrofitServices.AccountService.class);
        userService = RxRetrofitBuilder.create(this, RxRetrofitServices.UserService.class);
        recipeService = RxRetrofitBuilder.create(this, RxRetrofitServices.RecipeService.class);
        postService = RxRetrofitBuilder.create(this, RxRetrofitServices.PostService.class);

        String userID = Utils.getUserBrief(this).getUserID();
        if (getIntent().getStringExtra("userID") == null
                || getIntent().getStringExtra("userID").equals(userID)) {
            currentMode = MODE.OWNER;
            currentUserID = userID;
        } else {
            currentMode = MODE.OTHERS;
            currentUserID = getIntent().getStringExtra("userID");
        }

        recipeHorizontalHomeAdapter = new RecipeHorizontalHomeAdapter(currentUserID);
        recipeHorizontalHomeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(HomeActivity.this, RecipeDetailActivity.class)
                    .putExtra("recipeID", recipeList.get(position).getRecipeID());
            startActivity(intent);
        });
        binding.homeRecipeRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.homeRecipeRecycler.setAdapter(recipeHorizontalHomeAdapter);

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
            loadList();
        } else {
            userDetailInfo = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_USER_DETAIL));
            recipeList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATE_TYPE_RECIPE));
            postList = DataGenerator.make(getResources(), getResources().getInteger(R.integer.DATA_TYPE_POST));

            setUserInfo();
            recipeHorizontalHomeAdapter.setNewData(recipeList);
            postListAdapter.setNewData(postList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentMode == MODE.OWNER) {
            getMenuInflater().inflate(R.menu.menu_home_owner, menu);

            reviseMenu = menu.findItem(R.id.menu_home_owner_revise);
            okMenu = menu.findItem(R.id.menu_home_owner_revise_ok);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (currentOperation == OPERATION.REVISE) {
            createConfirmDialog(this,
                    null, "변경이 취소됩니다.",
                    (dialog1, which) -> {
                        currentOperation = OPERATION.VIEW;
                        reviseMenu.setVisible(true);
                        okMenu.setVisible(false);
                        binding.homeRevisegroup.setVisibility(View.GONE);

                        ImageLoadUtil.loadUserImage(this, userDetailInfo.getUserImg(), binding.homeUserimg);
                        binding.homeNickname.setText(userDetailInfo.getNickname());
                        binding.homeProfiletext.setText(userDetailInfo.getProfileText());
                    }
            )
                    .show();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home_owner_revise) {
            currentOperation = OPERATION.REVISE;
            reviseMenu.setVisible(false);
            okMenu.setVisible(true);
            binding.homeRevisegroup.setVisibility(View.VISIBLE);
        } else if (item.getItemId() == R.id.menu_home_owner_revise_ok) {
            updateProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                Utils.log(result.getUri());
                GlideApp.with(this)
                        .load(mImageUri)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.homeUserimg);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
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

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                    .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                    .setOutputUri(mImageUri)
                    .start(this);
        }
    }

    void loadList() {
        compositeDisposable.add(
                userService.getUserDetail(currentUserID)
                        .flatMap((Function<Response<UserDetail>, Single<Response<ArrayList<Recipe>>>>)
                                response -> {
                                    userDetailInfo = response.body();
                                    return recipeService.getRecipeByUserID(currentUserID, "latest");
                                })
                        .flatMap((Function<Response<ArrayList<Recipe>>, Single<Response<ArrayList<Post>>>>)
                                response -> {
                                    List<Recipe> res = response.body().subList(0, Math.min(response.body().size(), 10));
                                    recipeList = new ArrayList<>(res);

                                    return postService.getPostListByUserID(currentUserID)
                                            .observeOn(AndroidSchedulers.mainThread());
                                })
                        .subscribe(response -> {
                            postList = response.body();

                            setUserInfo();

                            recipeHorizontalHomeAdapter.setNewData(recipeList);
                            recipeHorizontalHomeAdapter.setEmptyView(
                                    R.layout.rv_empty_recipe, (ViewGroup) binding.homeRecipeRecycler.getParent());

                            postListAdapter.setNewData(postList);
                            postListAdapter.setEmptyView(
                                    R.layout.rv_empty_post, (ViewGroup) binding.homePostRecycler.getParent());
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }

    void setUserInfo() {
        ImageLoadUtil.loadUserImage(this, userDetailInfo.getUserImg(), binding.homeUserimg);

        binding.homeToolbar.setTitle(userDetailInfo.getNickname());
        binding.homeNickname.setText(userDetailInfo.getNickname());
        binding.homeProfiletext.setText(userDetailInfo.getProfileText());
        binding.homeRecipecount.setText(Integer.toString(userDetailInfo.getRecipeCount()));
        binding.homeFollowercount.setText(Integer.toString(userDetailInfo.getFollowerCount()));
        binding.homeFollowingcount.setText(Integer.toString(userDetailInfo.getFollowingCount()));

        binding.homeFollowBtn.setVisibility(currentMode == MODE.OTHERS ? View.VISIBLE : View.GONE);

        binding.homeUserimgRevise.setOnClickListener(this::reviseProfileImage);
        binding.homeNicknameRevise.setOnClickListener(this::clickReviseNickname);
        binding.homeProfiletextRevise.setOnClickListener(this::clickReviseContents);
        binding.homeRecipewrapper.setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, RecipeMyListActivity.class)
                    .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
        binding.homeFollowerwrapper.setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, FollowListActivity.class)
                    .putExtra("MODE", FollowListActivity.MODE.FOLLOWER)
                    .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
        binding.homeFollowingwrapper.setOnClickListener((v -> {
            Intent intent = new Intent(HomeActivity.this, FollowListActivity.class)
                    .putExtra("MODE", FollowListActivity.MODE.FOLLOWING)
                    .putExtra("userID", userDetailInfo.getUserID());
            startActivity(intent);
        }));
    }

    void reviseProfileImage(View v) {
        final String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (Utils.checkPermission(this, permissions)) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setOutputUri(mImageUri)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setRequestedSize(IMG_WIDTH, IMG_HEIGHT)
                    .setMaxCropResultSize(IMG_WIDTH, IMG_HEIGHT)
                    .start(this);
        } else
            ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION);
    }

    void clickReviseNickname(View v) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .title("닉네임 변경")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(4, 12)
                .input("닉네임", binding.homeNickname.getText().toString(), (dialog, input) -> {
                })
                .positiveText("확인")
                .positiveColorRes(R.color.grey_text)
                .onPositive((dialog, which) -> {
                    if (dialog.getInputEditText().getText() == null) {
                        App.getAppInstance().showToast("닉네임을 입력 해 주세요.");
                    } else if (dialog.getInputEditText().getText().toString().length() > 12) {
                        App.getAppInstance().showToast("12자 이하 입력 해 주세요.");
                    } else {
                        compositeDisposable.add(
                                accountService.checkNickname(dialog.getInputEditText().getText().toString())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(response -> {
                                            String msg = response.body().get("msg").getAsString();
                                            Utils.log(msg);
                                            if (msg.equals("ok")) {
                                                binding.homeNickname.setText(dialog.getInputEditText().getText().toString());
                                                dialog.dismiss();
                                            } else
                                                App.getAppInstance().showToast("이미 존재합니다.");
                                        }, RxRetrofitBuilder.defaultConsumer())
                        );
                    }
                })
                .negativeText("취소")
                .negativeColorRes(R.color.grey_text)
                .onNegative((dialog, which) -> dialog.dismiss())
                .build();

        materialDialog.show();
    }

    void clickReviseContents(View v) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .title("프로필 변경")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(0, 60)
                .input("프로필", binding.homeProfiletext.getText().toString(), (dialog, input) -> {
                })
                .positiveText("확인")
                .positiveColorRes(R.color.grey_text)
                .onPositive((dialog, which) -> {
                    if (dialog.getInputEditText().getText() == null) {
                        App.getAppInstance().showToast("프로필을 입력 해 주세요.");
                    } else if (dialog.getInputEditText().getText().toString().length() > 60) {
                        App.getAppInstance().showToast("60자 이하 입력 해 주세요.");
                    } else if (dialog.getInputEditText().getLineCount() > 4) {
                        App.getAppInstance().showToast("4줄 이하 입력 해 주세요.");
                    } else {
                        binding.homeProfiletext.setText(dialog.getInputEditText().getText().toString());
                        dialog.dismiss();
                    }
                })
                .negativeText("취소")
                .negativeColorRes(R.color.grey_text)
                .onNegative((dialog, which) -> dialog.dismiss())
                .build();

        materialDialog.show();
    }

    void updateProfile() {
        progressON(this);

        image_url = "";
        if (mImageUri != null) {
            image_url = String.format(getString(R.string.format_upload_file),
                    currentUserID, Long.toString(System.currentTimeMillis()));
            StorageReference ref = storageReference.child(getString(R.string.storage_path_profile) + image_url);
            ref.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> updateToServer());
        } else{
            image_url = userDetailInfo.getUserImg();
            updateToServer();
        }

    }

    void updateToServer() {
        compositeDisposable.add(
                accountService.updateUser(userDetailInfo.getUserID(), image_url,
                        binding.homeNickname.getText().toString(),
                        binding.homeProfiletext.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            App.getAppInstance().showToast("업데이트 되었습니다.");

                            currentOperation = OPERATION.VIEW;
                            reviseMenu.setVisible(true);
                            okMenu.setVisible(false);
                            binding.homeRevisegroup.setVisibility(View.VISIBLE);

                            mImageUri = null;
                            progressOFF();
                        }, RxRetrofitBuilder.defaultConsumer())
        );
    }
}
