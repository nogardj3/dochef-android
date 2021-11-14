package com.yhjoo.dochef.di

import android.content.Context
import com.yhjoo.dochef.data.RetrofitServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideBasicService(@ApplicationContext context: Context): RetrofitServices.BasicService {
        return RetrofitServices.BasicService.create(context)
    }

    @Singleton
    @Provides
    fun provideAccountService(@ApplicationContext context: Context): RetrofitServices.AccountService {
        return RetrofitServices.AccountService.create(context)
    }

    @Singleton
    @Provides
    fun provideUserService(@ApplicationContext context: Context): RetrofitServices.UserService {
        return RetrofitServices.UserService.create(context)
    }

    @Singleton
    @Provides
    fun provideRecipeService(@ApplicationContext context: Context): RetrofitServices.RecipeService {
        return RetrofitServices.RecipeService.create(context)
    }

    @Singleton
    @Provides
    fun provideReviewService(@ApplicationContext context: Context): RetrofitServices.ReviewService {
        return RetrofitServices.ReviewService.create(context)
    }

    @Singleton
    @Provides
    fun providePostService(@ApplicationContext context: Context): RetrofitServices.PostService {
        return RetrofitServices.PostService.create(context)
    }

    @Singleton
    @Provides
    fun provideCommentService(@ApplicationContext context: Context): RetrofitServices.CommentService {
        return RetrofitServices.CommentService.create(context)
    }

}
