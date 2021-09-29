package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
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
            if (App.isServerAlive)
                emit(accountClient.checkNickname(nickname))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
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
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}