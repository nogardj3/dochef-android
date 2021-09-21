package com.yhjoo.dochef.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.ArrayList

class FollowListRepository(private val context : Context) {
    val followLists: Flow<List<NotificationEntity>>? = null
    private val userClient = RetrofitBuilder.create(context, RetrofitServices.UserService::class.java)

    @WorkerThread
    suspend fun getUserDetail(userID: String){
        if(App.isServerAlive)
            userClient.getUserDetail(userID)
        else
            DataGenerator.make(context.resources,
                context.resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
    }

    @WorkerThread
    suspend fun getFollowers(targetId: String): Response<ArrayList<UserBrief>>? {
        return if(App.isServerAlive)
            userClient.getFollowers(targetId)
        else
            DataGenerator.make(context.resources,
                context.resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
    }

    @WorkerThread
    suspend fun getFollowings(targetId: String): Response<ArrayList<UserBrief>>?{
        return if(App.isServerAlive)
            userClient.getFollowings(targetId)
        else
            DataGenerator.make(context.resources,
                context.resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL))
    }

    @WorkerThread
    suspend fun subscribeUser(activeUserId:String,targetId: String):Response<JsonObject>?{
        return if(App.isServerAlive)
            userClient.subscribeUser(activeUserId,targetId)
        else
            null
    }

    @WorkerThread
    suspend fun unsubscribeUser(activeUserId:String,targetId: String): Response<JsonObject>?{
        return if(App.isServerAlive)
            userClient.unsubscribeUser(activeUserId,targetId)
        else
            null
    }
}