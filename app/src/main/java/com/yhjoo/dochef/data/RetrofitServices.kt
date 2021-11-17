package com.yhjoo.dochef.data

import android.content.Context
import com.google.gson.JsonObject
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitServices {
    interface BasicService {
        @GET("notice")
        suspend fun getNotice(): Response<ArrayList<ExpandableItem>?>

        @GET("faq")
        suspend fun getFAQ(): Response<ArrayList<ExpandableItem>?>

        @GET("tos")
        suspend fun getTOS(): Response<JsonObject?>

        @GET("alive")
        suspend fun checkAlive(): Response<JsonObject?>

        companion object {
            fun create(context: Context): BasicService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(BasicService::class.java)
            }
        }
    }

    interface AccountService {
        @FormUrlEncoded
        @POST("user/check/nickname")
        suspend fun checkNickname(@Field("nickname") nickname: String): Response<JsonObject?>

        @FormUrlEncoded
        @POST("user/check/")
        suspend fun checkUser(
            @Field("user_token") token: String,
            @Field("user_id") uid: String,
            @Field("user_fcm_token") fcmtoken: String
        ): Response<UserBrief?>

        @FormUrlEncoded
        @POST("user/signup")
        suspend fun createUser(
            @Field("user_token") token: String,
            @Field("user_fcm_token") fcmtoken: String,
            @Field("user_id") uid: String,
            @Field("nickname") nickname: String
        ): Response<UserBrief?>

        @FormUrlEncoded
        @POST("user/update")
        suspend fun updateUser(
            @Field("user_id") userID: String,
            @Field("user_img") userImg: String,
            @Field("nickname") nickname: String,
            @Field("bio") bio: String
        ): Response<JsonObject?>

        companion object {
            fun create(context: Context): AccountService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AccountService::class.java)
            }
        }
    }

    interface UserService {
        @GET("user/")
        suspend fun getUserByNickname(@Query("nickname") nickname: String): Response<ArrayList<UserBrief>?>

        @GET("user/follower")
        suspend fun getFollowers(@Query("target_id") target_id: String): Response<ArrayList<UserBrief>?>

        @GET("user/detail")
        suspend fun getUserDetail(@Query("user_id") user_id: String): Response<UserDetail?>

        @GET("user/following")
        suspend fun getFollowings(@Query("target_id") target_id: String): Response<ArrayList<UserBrief>?>

        @FormUrlEncoded
        @POST("user/subscribe")
        suspend fun subscribeUser(
            @Field("user_id") user_id: String,
            @Field("target_id") target_id: String
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("user/unsubscribe")
        suspend fun unsubscribeUser(
            @Field("user_id") user_id: String,
            @Field("target_id") target_id: String
        ): Response<JsonObject?>

        companion object {
            fun create(context: Context): UserService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(UserService::class.java)
            }
        }
    }

    interface RecipeService {
        @GET("recipe/detail")
        suspend fun getRecipeDetail(@Query("recipe_id") recipeId: Int): Response<RecipeDetail?>

        @GET("recipe/")
        suspend fun getRecipes(@Query("sort") sort: String): Response<ArrayList<Recipe>?>

        @GET("recipe/")
        suspend fun getRecipeByUserID(
            @Query("user_id") userId: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>?>

        @GET("recipe/")
        suspend fun getRecipeByName(
            @Query("recipe_name") recipeName: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>?>

        @GET("recipe/")
        suspend fun getRecipeByTag(
            @Query("tag") tag: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>?>

        @GET("recipe/")
        suspend fun getRecipeByIngredient(
            @Query("ingredient") ingredient: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>?>

        @FormUrlEncoded
        @POST("recipe/count")
        suspend fun addCount(@Field("recipe_id") recipeId: Int): Response<JsonObject?>

        @FormUrlEncoded
        @POST("recipe/like")
        suspend fun setLikeRecipe(
            @Field("recipe_id") recipeId: Int,
            @Field("user_id") userId: String,
            @Field("like") like: Int
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("recipe/create")
        suspend fun createRecipe(
            @Body recipeDetail: RecipeDetail
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("recipe/delete")
        suspend fun deleteRecipe(
            @Field("recipe_id") recipeId: Int,
            @Field("user_id") userId: String,
        ): Response<JsonObject?>

        companion object {
            fun create(context: Context): RecipeService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RecipeService::class.java)
            }
        }
    }

    interface ReviewService {
        @GET("review/")
        suspend fun getReview(@Query("recipe_id") recipeId: Int): Response<ArrayList<Review>?>

        @FormUrlEncoded
        @POST("review/create")
        suspend fun createReview(
            @Field("recipe_id") recipeID: Int,
            @Field("user_id") userID: String,
            @Field("contents") contents: String,
            @Field("rating") rating: Float,
            @Field("datetime") dateTime: Long
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("review/delete")
        suspend fun deleteReview(@Field("recipe_id") recipeId: Int): Response<JsonObject?>

        companion object {
            fun create(context: Context): ReviewService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ReviewService::class.java)
            }
        }
    }

    interface PostService {
        @GET("post/")
        suspend fun getPostList(): Response<ArrayList<Post>?>

        @GET("post/")
        suspend fun getPostListByUserID(@Query("user_id") userID: String): Response<ArrayList<Post>?>

        @GET("post/detail")
        suspend fun getPost(@Query("post_id") postID: Int): Response<Post?>

        @FormUrlEncoded
        @POST("post/like")
        suspend fun setLikePost(
            @Field("user_id") userID: String,
            @Field("post_id") postID: Int,
            @Field("like") like: Int
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("post/create")
        suspend fun createPost(
            @Field("user_id") userID: String,
            @Field("post_img") postImgs: String,
            @Field("contents") contents: String,
            @Field("datetime") datetime: Long,
            @Field("tags") tags: ArrayList<String>
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("post/update")
        suspend fun updatePost(
            @Field("post_id") postID: Int,
            @Field("post_img") postImgs: String,
            @Field("contents") contents: String,
            @Field("datetime") datetime: Long,
            @Field("tags") tags: ArrayList<String>
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("post/delete")
        suspend fun deletePost(@Field("post_id") postID: Int): Response<JsonObject?>

        companion object {
            fun create(context: Context): PostService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PostService::class.java)
            }
        }
    }

    interface CommentService {
        @GET("comment/")
        suspend fun getComment(@Query("post_id") postID: Int): Response<ArrayList<Comment>?>

        @FormUrlEncoded
        @POST("comment/create")
        suspend fun createComment(
            @Field("post_id") postID: Int,
            @Field("user_id") userID: String,
            @Field("contents") contents: String,
            @Field("datetime") dateTime: Long
        ): Response<JsonObject?>

        @FormUrlEncoded
        @POST("comment/delete")
        suspend fun deleteComment(@Field("comment_id") commentID: Int): Response<JsonObject?>

        companion object {
            fun create(context: Context): CommentService {
                val retrofitClient =
                    OkHttpClient.Builder()
                        .connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()

                return Retrofit.Builder()
                    .client(retrofitClient)
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(CommentService::class.java)
            }
        }
    }
}