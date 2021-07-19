package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.databinding.APostwriteBinding;
import com.yhjoo.dochef.interfaces.RxRetrofitServices;
import com.yhjoo.dochef.utils.ImageLoadUtil;
import com.yhjoo.dochef.utils.RxRetrofitBuilder;
import com.yhjoo.dochef.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class PostWriteActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;

    enum MODE {WRITE, REVISE}

    APostwriteBinding binding;
    StorageReference storageReference;
    RxRetrofitServices.PostService postService;

    Uri mImageUri;
    MODE current_mode = MODE.WRITE;
    String userID;
    String image_url;
    int postID;

    /*
        TODO
        revise 프로세스 확인
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = APostwriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference();

        postService = RxRetrofitBuilder.create(this, RxRetrofitServices.PostService.class);

        userID = Utils.getUserBrief(this).getUserID();
        current_mode = (MODE) getIntent().getSerializableExtra("MODE");

        if (current_mode == MODE.REVISE) {
            postID = getIntent().getIntExtra("postID", -1);

            binding.postwriteToolbar.setTitle("수정");
            binding.postwriteContents.setText(getIntent().getStringExtra("contents"));

            if (getIntent().getStringExtra("postImg") != null) {
                ImageLoadUtil.loadPostImage(
                        this, getIntent().getStringExtra("postImg"), binding.postwritePostimg);
            }

            binding.postwriteTags.setTags(getIntent().getStringArrayExtra("tags"));
        }
        setSupportActionBar(binding.postwriteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.postwritePostimgAdd.setOnClickListener(this::addImage);
        binding.postwriteOk.setOnClickListener(this::doneClicked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                binding.postwritePostimg.setImageURI(mImageUri);
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
                    .setAspectRatio(1,1)
                    .setRequestedSize(1080,1080)
                    .setOutputUri(mImageUri)
                    .start(this);
        }
    }

    void addImage(View v) {
        final String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (Utils.checkPermission(this, permissions)) {
            mImageUri = Uri.fromFile(new File(getExternalCacheDir(), "filterimage"));

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setRequestedSize(1080,1080)
                    .setOutputUri(mImageUri)
                    .start(this);
        } else
            ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION);
    }

    void doneClicked(View v) {
        ArrayList<String> tags = new ArrayList<>();
        tags.addAll(binding.postwriteTags.getTags());

        image_url = "";
        if (mImageUri != null) {
            image_url = String.format(getString(R.string.format_upload_file),
                    userID, Long.toString(System.currentTimeMillis()));
        }

        if (image_url != null && !image_url.equals("")) {
            StorageReference ref = storageReference.child(image_url);
            ref.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        createORupdatePost(tags);
                    });
        }
        else
            createORupdatePost(tags);
    }

    void createORupdatePost(ArrayList<String> tags){
        if (current_mode == MODE.WRITE)
            compositeDisposable.add(
                    postService.createPost(userID, image_url,
                            binding.postwriteContents.getText().toString(),
                            System.currentTimeMillis(), tags)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                App.getAppInstance().showToast("글이 등록되었습니다.");
                                finish();
                            }, RxRetrofitBuilder.defaultConsumer())
            );
        else
            compositeDisposable.add(
                    postService.updatePost(postID, image_url,
                            binding.postwriteContents.getText().toString(),
                            System.currentTimeMillis(), tags)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                App.getAppInstance().showToast("업데이트 되었습니다.");
                                finish();
                            }, RxRetrofitBuilder.defaultConsumer())
            );
    }
}
