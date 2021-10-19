package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class AccountRepository(
    private val context: Context,
) {
    private val accountClient =
        RetrofitBuilder.create(context, RetrofitServices.AccountService::class.java)

    @WorkerThread
    suspend fun checkNickname(nickname: String): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive) emit(accountClient.checkNickname(nickname))
            else emit(Response.success(JsonObject()))
        }
    }

    @WorkerThread
    suspend fun createUser(
        token: String,
        fcmtoken: String,
        uid: String,
        nickname: String
    ): Flow<Response<UserBrief>> {
        return flow {
            if (App.isServerAlive)
                emit(accountClient.createUser(token, fcmtoken, uid, nickname))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun updateUser(
        userID: String,
        userImg: String,
        nickname: String,
        bio: String
    ): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(accountClient.updateUser(userID, userImg, nickname, bio))
            else
                emit(Response.success(JsonObject()))
        }
    }

    @WorkerThread
    suspend fun checkUser(
        token: String,
        uid: String,
        fcmtoken: String
    ): Flow<Response<UserBrief>> {
        return flow {
            if (App.isServerAlive)
                emit(accountClient.checkUser(token, uid, fcmtoken))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)
                        )
                    )
                )
        }
    }
}