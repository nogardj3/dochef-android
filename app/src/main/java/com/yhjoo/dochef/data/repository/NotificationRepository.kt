package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.dao.NotificationDao
import com.yhjoo.dochef.data.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepository(
    private val context: Context,
    private val notificationDao: NotificationDao
) {
    val notifications: Flow<List<NotificationEntity>> = getData()

    @WorkerThread
    fun insert(notificationEntity: NotificationEntity): Long {
        return notificationDao.insert(notificationEntity)
    }

    @WorkerThread
    fun update(notificationEntity: NotificationEntity): Int {
        return notificationDao.update(notificationEntity)
    }

    @WorkerThread
    fun delete(notificationEntity: NotificationEntity): Int {
        return notificationDao.delete(notificationEntity)
    }

    @WorkerThread
    fun setRead(id: Long) {
        notificationDao.setRead(id)
    }

    private fun getData(): Flow<List<NotificationEntity>> {
        return if (App.isServerAlive) {
            notificationDao.getRecentList(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3)
        } else
            flow {
                emit(
                    DataGenerator.make(
                        context.resources,
                        context.resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION)
                    ) as List<NotificationEntity>
                )
            }
    }
}