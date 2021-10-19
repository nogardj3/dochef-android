package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.ExpandableItem
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*

class BasicRepository(
    private val context: Context
) {
    private val basicClient =
        RetrofitBuilder.create(context, RetrofitServices.BasicService::class.java)

    @WorkerThread
    suspend fun getFAQs(): Flow<Response<ArrayList<ExpandableItem>>> {
        return flow {
            if (App.isServerAlive) emit(basicClient.getFAQ())
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
    suspend fun getNotices(): Flow<Response<ArrayList<ExpandableItem>>> {
        return flow {
            if (App.isServerAlive) emit(basicClient.getNotice())
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
    suspend fun getTOS(): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive) emit(basicClient.getTOS())
            else {
                val jsonObject = JsonObject().apply {
                    addProperty("message", "이용약관")
                }
                emit(
                    Response.success(
                        jsonObject
                    )
                )
            }
        }
    }
}