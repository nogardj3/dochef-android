package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.RetrofitServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reviewService: RetrofitServices.ReviewService
) {
    @WorkerThread
    suspend fun getReviews(recipeId: Int): Flow<Response<ArrayList<Review>?>> {
        return flow {
            if (App.isServerAlive) emit(reviewService.getReview(recipeId))
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
        rating: Float,
        dateTime: Long
    ): Flow<Response<JsonObject?>> {
        return flow {
            if (App.isServerAlive) emit(
                reviewService.createReview(
                    recipeID,
                    userID,
                    contents,
                    rating,
                    dateTime
                )
            )
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun deleteReview(recipeID: Int): Flow<Response<JsonObject?>> {
        return flow {
            if (App.isServerAlive) emit(reviewService.deleteReview(recipeID))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}