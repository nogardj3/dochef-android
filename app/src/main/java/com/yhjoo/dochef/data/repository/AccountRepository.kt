package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class AccountRepository(
    context: Context,
) {
    private val accountClient =
        RetrofitBuilder.create(context, RetrofitServices.AccountService::class.java)

    @WorkerThread
    suspend fun createUser(
        token: String,
        fcmtoken: String,
        uid: String,
        nickname: String
    ): Flow<Response<UserBrief?>> {
        return flow {
            emit(accountClient.createUser(token, fcmtoken, uid, nickname))
        }
    }

    @WorkerThread
    suspend fun checkUser(
        token: String,
        uid: String,
        fcmtoken: String
    ): Flow<Response<UserBrief?>> {
        return flow {
            emit(accountClient.checkUser(token, uid, fcmtoken))
        }
    }

    @WorkerThread
    suspend fun updateUser(
        userID: String,
        userImg: String,
        nickname: String,
        bio: String
    ): Flow<Response<JsonObject?>> {
        return flow {
            emit(accountClient.updateUser(userID, userImg, nickname, bio))
        }
    }

    @WorkerThread
    suspend fun checkNickname(nickname: String): Flow<Response<JsonObject?>> {
        return flow {
            emit(accountClient.checkNickname(nickname))
        }
    }

}