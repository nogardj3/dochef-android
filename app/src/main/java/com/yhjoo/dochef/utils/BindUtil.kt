package com.yhjoo.dochef.utils

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.google.firebase.storage.FirebaseStorage
import com.yhjoo.dochef.App
import com.yhjoo.dochef.GlideApp
import com.yhjoo.dochef.R
import java.text.SimpleDateFormat
import java.util.*

object BindUtil {
    fun loadUserImage(
        filename: String,
        view: AppCompatImageView
    ) {

        if(filename.isNotEmpty()){
            val profileSrc = FirebaseStorage.getInstance().reference
                .child(view.context.getString(R.string.storage_path_profile) + filename)

            GlideApp.with(view.context)
                .load(if (App.isServerAlive) profileSrc else Integer.valueOf(filename))
                .error(R.drawable.ic_profile_black)
                .circleCrop()
                .into(view)

        }
    }

    @BindingConversion
    @JvmStatic
    fun millisToText(millis: Long): String {
        val currentMillis = Date().time
        val secDiff = (currentMillis - millis) / 1000

        return when {
            secDiff < 60 -> "방금 전"
            secDiff / 60 < 60 -> (secDiff / 60).toString() + "분 전"
            secDiff / 60 / 60 < 24 -> (secDiff / 60 / 60).toString() + "시간 전"
            secDiff / 60 / 60 / 24 < 7 -> (secDiff / 60 / 60 / 24).toString() + "일 전"
            else -> {
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                formatter.format(Date(millis))
            }
        }
    }

    @BindingConversion
    @JvmStatic
    fun visibleBoolToInt(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("floatText")
    @JvmStatic
    fun floatToText(
        view: AppCompatTextView,
        float: Float
    ){
        view.text = String.format("%.1f",float)
    }

    @BindingAdapter("visibleNew")
    @JvmStatic
    fun visibleNew(
        view: View,
        millis: Long,
    ) {
        val currentMillis = Date().time
        val secDiff = (currentMillis - millis) / 1000

        return if(secDiff / 60 / 60 / 24 < 3)
            view.visibility = View.VISIBLE
        else
            view.visibility = View.GONE
    }

    @BindingAdapter("srcDrawable")
    @JvmStatic
    fun srcWithDrawable (
        view: AppCompatImageView,
        drawable:Drawable,
    )  {
        view.setImageDrawable(drawable)
    }

    @BindingAdapter("userimage")
    @JvmStatic
    fun loadUserImage(
        view: AppCompatImageView,
        filename: String?,
    ) {
        if (filename != null && filename.isNotEmpty()) {
            val context = view.context

            val profileSrc = FirebaseStorage.getInstance().reference
                .child(context.getString(R.string.storage_path_profile) + filename)

            GlideApp.with(context)
                .load(if (App.isServerAlive) profileSrc else Integer.valueOf(filename))
                .error(R.drawable.ic_profile_black)
                .circleCrop()
                .into(view)
        }
        else
            view.setImageResource(R.drawable.ic_profile_black)
    }

    @BindingAdapter("recipeimage")
    @JvmStatic
    fun loadRecipeImage(
        appCompatImageView: AppCompatImageView,
        filename: String?,
    ) {
        if (filename !=null && filename.isNotEmpty()) {
            val context = appCompatImageView.context

            val recipeSrc = FirebaseStorage.getInstance().reference
                .child(context.getString(R.string.storage_path_recipe) + filename)

            GlideApp.with(context)
                .load(if (App.isServerAlive) recipeSrc else Integer.valueOf(filename))
                .error(R.drawable.ic_error)
                .into(appCompatImageView)
        }
    }

    @BindingAdapter("postimage")
    @JvmStatic
    fun loadPostImage(
        appCompatImageView: AppCompatImageView,
        filename: String?,
    ) {
        if (filename != null && filename.isNotEmpty()) {
            val context = appCompatImageView.context

            val postSrc = FirebaseStorage.getInstance().reference
                .child(context.getString(R.string.storage_path_post) + filename)

            GlideApp.with(context)
                .load(if (App.isServerAlive) postSrc else Integer.valueOf(filename))
                .into(appCompatImageView)
        }
    }
}