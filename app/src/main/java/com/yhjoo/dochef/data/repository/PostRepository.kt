package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Post
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*

class PostRepository(
    private val context: Context
) {
    private val postClient =
        RetrofitBuilder.create(context, RetrofitServices.PostService::class.java)

    @WorkerThread
    suspend fun getPostDetail(postId: Int): Flow<Response<Post>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.getPost(postId))
            else
                emit(
                    Response.success(
                        (DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_POST)
                        ) as ArrayList<Post>)[0]
                    )
                )
        }
    }

    @WorkerThread
    suspend fun getPostList(): Flow<Response<ArrayList<Post>>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.getPostList())
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_POST)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun getPostListByUserId(userId: String): Flow<Response<ArrayList<Post>>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.getPostListByUserID(userId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_POST)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun likePost(userID: String, postID: Int): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.setLikePost(userID, postID, 1))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun dislikePost(userID: String, postID: Int): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.setLikePost(userID, postID, -1))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun createPost(
        userID: String,
        postImgs: String,
        contents: String,
        datetime: Long,
        tags: ArrayList<String>
    ): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.createPost(userID, postImgs, contents, datetime, tags))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }


    @WorkerThread
    suspend fun updatePost(
        postID: Int,
        postImgs: String,
        contents: String,
        datetime: Long,
        tags: ArrayList<String>
    ): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.updatePost(postID, postImgs, contents, datetime, tags))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun deletePost(postID: Int): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(postClient.deletePost(postID))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}