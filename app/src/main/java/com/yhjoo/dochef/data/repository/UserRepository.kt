package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.model.UserDetail
import com.yhjoo.dochef.data.RetrofitServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userClient: RetrofitServices.UserService
) {
    @WorkerThread
    suspend fun getUserDetail(userId: String): Flow<Response<UserDetail?>> {
        return flow {
            if (App.isServerAlive) emit(userClient.getUserDetail(userId))
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
    suspend fun getUserByNickname(nickname: String): Flow<Response<ArrayList<UserBrief>?>> {
        return flow {
            if (App.isServerAlive) emit(userClient.getUserByNickname(nickname))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF_LIST)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun getFollowers(userId: String): Flow<Response<ArrayList<UserBrief>?>> {
        return flow {
            if (App.isServerAlive) emit(userClient.getFollowers(userId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF_LIST)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun getFollowings(userId: String): Flow<Response<ArrayList<UserBrief>?>> {
        return flow {
            if (App.isServerAlive) emit(userClient.getFollowings(userId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF_LIST)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun subscribeUser(userId: String, targetId: String): Flow<Response<JsonObject?>> {
        return flow {
            if (App.isServerAlive) emit(userClient.subscribeUser(userId, targetId))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun unsubscribeUser(userId: String, targetId: String): Flow<Response<JsonObject?>> {
        return flow {
            if (App.isServerAlive) emit(userClient.unsubscribeUser(userId, targetId))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}