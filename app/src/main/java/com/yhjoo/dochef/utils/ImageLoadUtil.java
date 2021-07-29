package com.yhjoo.dochef.utils;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yhjoo.dochef.App;
import com.yhjoo.dochef.R;


public class ImageLoadUtil {
    public static void loadUserImage(Context context, String filename, AppCompatImageView appCompatImageView) {
        if (App.isServerAlive()) {
            if (!filename.equals("default")) {
                StorageReference profile_sr = FirebaseStorage.getInstance().getReference()
                        .child(context.getString(R.string.storage_path_profile) + filename);

                GlideApp.with(context)
                        .load(profile_sr)
                        .circleCrop()
                        .into(appCompatImageView);
            }
        } else
            GlideApp.with(context)
                    .load(Integer.valueOf(filename))
                    .circleCrop()
                    .into(appCompatImageView);
    }

    public static void loadRecipeImage(Context context, String filename, AppCompatImageView appCompatImageView) {
        if (App.isServerAlive()) {
            StorageReference recipe_sr = FirebaseStorage.getInstance().getReference()
                    .child(context.getString(R.string.storage_path_recipe) + filename);

            GlideApp.with(context)
                    .load(recipe_sr)
                    .into(appCompatImageView);
        } else
            GlideApp.with(context)
                    .load(Integer.valueOf(filename))
                    .into(appCompatImageView);
    }

    public static void loadPostImage(Context context, String filename, AppCompatImageView appCompatImageView) {
        if (App.isServerAlive()) {
            if (!filename.equals("")) {
                StorageReference post_sr = FirebaseStorage.getInstance().getReference()
                        .child(context.getString(R.string.storage_path_post) + filename);

                GlideApp.with(context)
                        .load(post_sr)
                        .into(appCompatImageView);
            }
        } else
            GlideApp.with(context)
                    .load(Integer.valueOf(filename))
                    .into(appCompatImageView);
    }
}