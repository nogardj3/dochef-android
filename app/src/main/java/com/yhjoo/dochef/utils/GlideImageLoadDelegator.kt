package com.yhjoo.dochef.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.storage.FirebaseStorage
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R

object GlideImageLoadDelegator {
    fun loadUserImage(
        context: Context,
        filename: String,
        appCompatImageView: AppCompatImageView
    ) {
        if (App.isServerAlive) {
            if (filename != "default") {
                val profileSrc = FirebaseStorage.getInstance().reference
                    .child(context.getString(R.string.storage_path_profile) + filename)

                GlideApp.with(context)
                    .load(profileSrc)
                    .circleCrop()
                    .into(appCompatImageView)
            }
        } else
            GlideApp.with(context)
                .load(Integer.valueOf(filename))
                .circleCrop()
                .into(appCompatImageView)
    }

    fun loadRecipeImage(
        context: Context,
        filename: String,
        appCompatImageView: AppCompatImageView
    ) {
        if (App.isServerAlive) {
            val recipeSrc = FirebaseStorage.getInstance().reference
                .child(context.getString(R.string.storage_path_recipe) + filename)

            GlideApp.with(context)
                .load(recipeSrc)
                .into(appCompatImageView)
        } else
            GlideApp.with(context)
                .load(Integer.valueOf(filename))
                .into(appCompatImageView)
    }

    fun loadPostImage(
        context: Context,
        filename: String,
        appCompatImageView: AppCompatImageView
    ) {
        if (App.isServerAlive) {
            if (filename != "") {
                val postSrc = FirebaseStorage.getInstance().reference
                    .child(context.getString(R.string.storage_path_post) + filename)

                GlideApp.with(context)
                    .load(postSrc)
                    .into(appCompatImageView)
            }
        } else GlideApp.with(context)
            .load(Integer.valueOf(filename))
            .into(appCompatImageView)
    }
}