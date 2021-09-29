package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Comment
import com.yhjoo.dochef.data.model.Review
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.Field
import java.util.*

class CommentRepository(
    private val context: Context
) {
    private val commentClient =
        RetrofitBuilder.create(context, RetrofitServices.CommentService::class.java)

    @WorkerThread
    suspend fun getComments(postId: Int): Flow<Response<ArrayList<Comment>>> {
        return flow {
            if (App.isServerAlive)
                emit(commentClient.getComment(postId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_COMMENTS)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun createComment(
        postID: Int,
        userID: String,
        contents: String,
        dateTime: Long
    ): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(commentClient.createComment(postID, userID, contents, dateTime))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun deleteComment(commentId: Int): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(commentClient.deleteComment(commentId))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}