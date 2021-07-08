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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.databinding.AReviewwriteBinding;
import com.yhjoo.dochef.utils.PermissionUtil;

import java.io.File;

public class ReviewWriteActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;

    AReviewwriteBinding binding;

    Uri mImageUri;

    /*
        TODO
        1. 서버 데이터 추가 및 기능 구현
        2. retrofit 구현
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AReviewwriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.reviewwriteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.reviewwriteReviewimgAdd.setOnClickListener(this::startImageAdd);
        binding.reviewwriteReviewimgAdd.setOnClickListener(this::uploadReview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRA_RQ_PICKFROMGALLERY)
            if (data != null) {
                binding.reviewwriteReviewimg.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(mImageUri != null ? mImageUri : data.getData())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(binding.reviewwriteReviewimg);
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

            mImageUri = Uri.fromFile(new File(getExternalCacheDir(), "filterimage"));

            Intent intent = new Intent(Intent.ACTION_PICK)
                    .setType(MediaStore.Images.Media.CONTENT_TYPE)
                    .putExtra("crop", "true")
                    .putExtra("aspectX", 3)
                    .putExtra("aspectY", 2)
                    .putExtra("scale", true)
                    .putExtra("output", mImageUri);
            startActivityForResult(intent, EXTRA_RQ_PICKFROMGALLERY);
        }
    }

    void startImageAdd(View v){
        final String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (PermissionUtil.checkPermission(this, permissions)) {
            mImageUri = Uri.fromFile(new File(getExternalCacheDir(), "filterimage"));

            Intent intent = new Intent(Intent.ACTION_PICK)
                    .setType(MediaStore.Images.Media.CONTENT_TYPE)
                    .putExtra("crop", "true")
                    .putExtra("aspectX", 3)
                    .putExtra("aspectY", 2)
                    .putExtra("scale", true)
                    .putExtra("output", mImageUri);
            startActivityForResult(intent, EXTRA_RQ_PICKFROMGALLERY);
        } else
            ActivityCompat.requestPermissions(this, permissions, CODE_PERMISSION);
    }

    void uploadReview(View v){
        App.getAppInstance().showToast("리뷰가 등록되었습니다.");
        finish();
    }
}
