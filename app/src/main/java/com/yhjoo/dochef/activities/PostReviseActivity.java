package com.yhjoo.dochef.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;
import com.yhjoo.dochef.base.BaseActivity;
import com.yhjoo.dochef.utils.PermissionUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostReviseActivity extends BaseActivity {
    private final int CODE_PERMISSION = 22;
    private final int EXTRA_RQ_PICKFROMGALLERY = 200;
    @BindView(R.id.revisepost_postimg)
    AppCompatImageView postimg;
    @BindView(R.id.revisepost_contents)
    AppCompatEditText contents;
    private Uri mImageUri;

    /*
        TODO
        이거 없어질것 PostWriteActivity에 합침
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_revisepost);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.revisepost_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getIntent().getIntExtra("postid",0);
        if (getIntent().getStringExtra("postimg") != null) {
            Log.w("dd", getIntent().getStringExtra("postimg"));
            postimg.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(getIntent().getStringExtra("postimg"))
                    .apply(RequestOptions.errorOf(R.drawable.ic_mood_bad_black_24dp).centerCrop())
                    .into(postimg);
        }

        contents.setText(getIntent().getStringExtra("contents"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRA_RQ_PICKFROMGALLERY)
            if (data != null) {
                postimg.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(mImageUri != null ? mImageUri : data.getData())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(postimg);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    App.getAppInstance().showToast("권한 거부");
                    return;
                }
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

    @OnClick({R.id.revisepost_postimg_add, R.id.revisepost_ok})
    void ok(View v) {
        switch (v.getId()) {
            case R.id.revisepost_postimg_add:
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
                break;
            case R.id.revisepost_ok:
                App.getAppInstance().showToast("글이 등록되었습니다.");
                break;
        }
    }
}
