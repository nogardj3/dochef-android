package com.yhjoo.dochef.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.StorageReference
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    // Todo
//  FirebaseImageLoader 이런게 없음
//    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//        registry.append(
//            StorageReference::class.java,
//            InputStream::class.java,
//            FirebaseImageLoader.Factory()
//        )
//    }
//
//    override fun applyOptions(context: Context, builder: GlideBuilder) {
//        super.applyOptions(context, builder)
//        //        builder.setLogLevel(Log.ERROR);
//    }
}