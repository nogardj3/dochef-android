package com.yhjoo.dochef.data.repository

import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.data.model.UserBrief
import com.yhjoo.dochef.data.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountService : RetrofitServices.AccountService
) {
    @WorkerThread
    suspend fun createUser(
        token: String,
        fcmtoken: String,
        uid: String,
        nickname: String
    ): Flow<Response<UserBrief?>> {
        return flow {
            emit(accountService.createUser(token, fcmtoken, uid, nickname))
        }
    }

    @WorkerThread
    suspend fun checkUser(
        token: String,
        uid: String,
        fcmtoken: String
    ): Flow<Response<UserBrief?>> {
        return flow {
            emit(accountService.checkUser(token, uid, fcmtoken))
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
            emit(accountService.updateUser(userID, userImg, nickname, bio))
        }
    }

    @WorkerThread
    suspend fun checkNickname(nickname: String): Flow<Response<JsonObject?>> {
        return flow {
            emit(accountService.checkNickname(nickname))
        }
    }

}