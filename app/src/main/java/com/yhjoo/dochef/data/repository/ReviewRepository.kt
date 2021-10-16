package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*

class ReviewRepository(
    private val context: Context
) {
    private val reviewClient =
        RetrofitBuilder.create(context, RetrofitServices.ReviewService::class.java)

    @WorkerThread
    suspend fun getReviews(recipeId: Int): Flow<Response<ArrayList<Review>>> {
        return flow {
            if (App.isServerAlive) emit(reviewClient.getReview(recipeId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_REVIEW)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun createReview(
        recipeID: Int,
        userID: String,
        contents: String,
        rating: Long,
        dateTime: Long
    ): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive) emit(reviewClient.createReview(recipeID, userID, contents, rating, dateTime))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun deleteReview(recipeID: Int): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive) emit(reviewClient.deleteReview(recipeID))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}