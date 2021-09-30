package com.yhjoo.dochef.utils

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.storage.FirebaseStorage
import com.yhjoo.dochef.App
import com.yhjoo.dochef.GlideApp
import com.yhjoo.dochef.R

object ImageLoaderUtil {
    fun loadUserImage(
        context: Context,
        filename: String,
        appCompatImageView: AppCompatImageView
    ) {
        val profileSrc = FirebaseStorage.getInstance().reference
            .child(context.getString(R.string.storage_path_profile) + filename)
        GlideApp.with(context)
            .load(if (App.isServerAlive) profileSrc else Integer.valueOf(filename))
            .error(R.drawable.ic_profile_black)
            .circleCrop()
            .into(appCompatImageView)
    }

    fun loadRecipeImage(
        context: Context,
        filename: String,
        appCompatImageView: AppCompatImageView
    ) {
        val recipeSrc = FirebaseStorage.getInstance().reference
            .child(context.getString(R.string.storage_path_recipe) + filename)

        GlideApp.with(context)
            .load(if (App.isServerAlive) recipeSrc else Integer.valueOf(filename))
            .error(R.drawable.ic_error)
            .into(appCompatImageView)
    }

    fun loadPostImage(
        context: Context,
        filename: String,
        appCompatImageView: AppCompatImageView
    ) {
        val postSrc = FirebaseStorage.getInstance().reference
            .child(context.getString(R.string.storage_path_post) + filename)

        GlideApp.with(context)
            .load(if (App.isServerAlive) postSrc else Integer.valueOf(filename))
            .into(appCompatImageView)
    }
}