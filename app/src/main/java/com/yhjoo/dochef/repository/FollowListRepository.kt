package com.yhjoo.dochef.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.UserBrief
import com.yhjoo.dochef.model.UserDetail
import com.yhjoo.dochef.ui.activities.FollowListActivity
import com.yhjoo.dochef.utilities.RetrofitBuilder
import com.yhjoo.dochef.utilities.RetrofitServices
import com.yhjoo.dochef.utilities.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*

class FollowListRepository(
    private val context: Context,
    private val uiMode: Int,
    private val activeUserId: String,
    private val currentUserId: String,
) {
    private val userClient =
        RetrofitBuilder.create(context, RetrofitServices.UserService::class.java)

    @WorkerThread
    suspend fun getUserDetail(): Flow<Response<UserDetail>> {
        return flow{
            if (App.isServerAlive)
                emit(userClient.getUserDetail(activeUserId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun getFollowList(): Flow<Response<ArrayList<UserBrief>>> {
        return flow{
            if (uiMode == FollowListActivity.UIMODE.FOLLOWER)
                emit(getFollowers())
            else
                emit(getFollowings())
        }
    }

    @WorkerThread
    suspend fun getFollowers(): Response<ArrayList<UserBrief>> {
        return if (App.isServerAlive)
            userClient.getFollowers(currentUserId)
        else
            Response.success(
                DataGenerator.make(
                    context.resources,
                    context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
                )
            )
    }

    @WorkerThread
    suspend fun getFollowings(): Response<ArrayList<UserBrief>> {
        return if (App.isServerAlive)
            userClient.getFollowings(currentUserId)
        else
            Response.success(
                DataGenerator.make(
                    context.resources,
                    context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
                )
            )
    }

    @WorkerThread
    suspend fun subscribeUser(targetId: String): Response<JsonObject>? {
        Utils.log("sub!!!!!!!!!!!!!!!")
        return if (App.isServerAlive)
            userClient.subscribeUser(activeUserId, targetId)
        else
            Response.error(444, null)
    }

    @WorkerThread
    suspend fun unsubscribeUser(targetId: String): Response<JsonObject>? {
        Utils.log("unSub!!!!!!!!!!!!!!!")
        return if (App.isServerAlive)
            userClient.unsubscribeUser(activeUserId, targetId)
        else
            Response.error(444, null)
    }
}