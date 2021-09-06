package com.yhjoo.dochef.utils

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class MyAppGlideModule : AppGlideModule()
//{
//    // Todo
////  FirebaseImageLoader 이런게 없음
//    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
////        registry.append(
////            StorageReference::class.java,
////            InputStream::class.java,
////            FirebaseImageLoader.Factory()
////        )
//    }
//
//    override fun applyOptions(context: Context, builder: GlideBuilder) {
//        super.applyOptions(context, builder)
////        builder.setLogLevel(Log.ERROR);
//    }
//}