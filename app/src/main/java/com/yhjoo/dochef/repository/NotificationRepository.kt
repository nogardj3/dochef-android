package com.yhjoo.dochef.repository

import com.yhjoo.dochef.db.dao.NotificationDao
import com.yhjoo.dochef.db.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository (private val dao: NotificationDao) {

    val subscribers = dao.getAllNotifications()

    suspend fun insert(notificationEntity: NotificationEntity): Long {
        return dao.insert(notificationEntity)
    }

    suspend fun update(notificationEntity: NotificationEntity): Int {
        return dao.update(notificationEntity)
    }

    suspend fun delete(notificationEntity: NotificationEntity): Int {
        return dao.delete(notificationEntity)
    }

    suspend fun getRecentList(dateTime: Long): Flow<List<NotificationEntity>> {
        return dao.getRecentList(dateTime)
    }
}