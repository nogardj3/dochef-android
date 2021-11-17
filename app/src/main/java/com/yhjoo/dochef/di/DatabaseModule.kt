package com.yhjoo.dochef.di

import android.content.Context
import com.yhjoo.dochef.data.ChefDatabase
import com.yhjoo.dochef.data.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ChefDatabase {
        return ChefDatabase.getInstance(context)
    }

    @Provides
    fun providePlantDao(appDatabase: ChefDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }
}
