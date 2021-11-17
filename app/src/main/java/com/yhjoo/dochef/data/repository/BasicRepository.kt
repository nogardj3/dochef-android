package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.RetrofitServices
import com.yhjoo.dochef.data.model.ExpandableItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val basicService: RetrofitServices.BasicService
) {
    @WorkerThread
    suspend fun checkAlive(): Flow<Response<JsonObject?>> {
        return flow {
            emit(basicService.checkAlive())
        }
    }

    @WorkerThread
    suspend fun getFAQs(): Flow<Response<ArrayList<ExpandableItem>?>> {
        return flow {
            if (App.isServerAlive) emit(basicService.getFAQ())
            else {
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_FAQ)
                        )
                    )
                )
            }
        }
    }

    @WorkerThread
    suspend fun getNotices(): Flow<Response<ArrayList<ExpandableItem>?>> {
        return flow {
            if (App.isServerAlive) emit(basicService.getNotice())
            else {
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_NOTICE)
                        )
                    )
                )
            }
        }
    }

    @WorkerThread
    suspend fun getTOS(): Flow<Response<JsonObject?>> {
        return flow {
            if (App.isServerAlive) emit(basicService.getTOS())
            else {
                emit(
                    Response.success(
                        JsonObject().apply {
                            addProperty("message", "이용약관")
                        }
                    )
                )
            }
        }
    }
}