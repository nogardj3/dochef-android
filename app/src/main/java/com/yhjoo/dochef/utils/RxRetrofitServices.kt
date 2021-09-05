package com.yhjoo.dochef.utils

import com.google.gson.JsonObject
import com.yhjoo.dochef.data.model.*
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*

class RxRetrofitServices {
    // TODO
    // 1. Coroutine용으로 변경

    interface BasicService {
        @get:GET("notice")
        val notice: Single<Response<ArrayList<Notice>>>

        @get:GET("faq")
        val fAQ: Single<Response<ArrayList<FAQ>>>

        @get:GET("tos")
        val tOS: Single<Response<JsonObject>>

        @GET("alive")
        fun checkAlive(): Single<Response<JsonObject?>?>
    }

    interface AccountService {
        @POST("user/check/nickname")
        fun checkNickname(@Query("nickname") nickname: String?): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("user/check/")
        fun checkUser(
            @Field("user_token") token: String?,
            @Field("user_id") uid: String?,
            @Field("user_fcm_token") fcmtoken: String?
        ): Single<Response<UserBrief?>?>

        @FormUrlEncoded
        @POST("user/signup")
        fun createUser(
            @Field("user_token") token: String?,
            @Field("user_fcm_token") fcmtoken: String?,
            @Field("user_id") uid: String?,
            @Field("nickname") nickname: String?
        ): Single<Response<UserBrief?>?>

        @FormUrlEncoded
        @POST("user/update")
        fun updateUser(
            @Field("user_id") userID: String?,
            @Field("user_img") userImg: String?,
            @Field("nickname") nickname: String?,
            @Field("bio") bio: String?
        ): Single<Response<JsonObject?>?>
    }

    interface UserService {
        @GET("user/")
        fun getUserByNickname(@Query("nickname") nickname: String?): Single<Response<ArrayList<UserBrief?>?>?>

        @GET("user/follower")
        fun getFollowers(@Query("target_id") target_id: String?): Single<Response<ArrayList<UserBrief?>?>?>

        @GET("user/detail")
        fun getUserDetail(@Query("user_id") user_id: String?): Single<Response<UserDetail?>?>

        @GET("user/following")
        fun getFollowings(@Query("target_id") target_id: String?): Single<Response<ArrayList<UserBrief?>?>?>

        @FormUrlEncoded
        @POST("user/subscribe")
        fun subscribeUser(
            @Field("user_id") user_id: String?,
            @Field("target_id") target_id: String?
        ): Single<Response<JsonObject?>?>?

        @FormUrlEncoded
        @POST("user/unsubscribe")
        fun unsubscribeUser(
            @Field("user_id") user_id: String?,
            @Field("target_id") target_id: String?
        ): Single<Response<JsonObject?>?>?
    }

    interface RecipeService {
        @GET("recipe/detail")
        fun getRecipeDetail(@Query("recipe_id") recipeId: Int): Single<Response<RecipeDetail?>?>

        @GET("recipe/")
        fun getRecipes(@Query("sort") sort: String?): Single<Response<ArrayList<Recipe?>?>?>

        @GET("recipe/")
        fun getRecipeByUserID(
            @Query("user_id") userId: String?,
            @Query("sort") sort: String?
        ): Single<Response<ArrayList<Recipe?>?>?>

        @GET("recipe/")
        fun getRecipeByName(
            @Query("recipe_name") recipeName: String?,
            @Query("sort") sort: String?
        ): Single<Response<ArrayList<Recipe?>?>?>?

        @GET("recipe/")
        fun getRecipeByTag(
            @Query("tag") tag: String?,
            @Query("sort") sort: String?
        ): Single<Response<ArrayList<Recipe?>?>?>

        @GET("recipe/")
        fun getRecipeByIngredient(
            @Query("ingredient") ingredient: String?,
            @Query("sort") sort: String?
        ): Single<Response<ArrayList<Recipe?>?>?>?

        @FormUrlEncoded
        @POST("recipe/count")
        fun addCount(@Field("recipe_id") recipeId: Int): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("recipe/like")
        fun setLikeRecipe(
            @Field("recipe_id") recipeId: Int,
            @Field("user_id") userId: String?,
            @Field("like") like: Int
        ): Single<Response<JsonObject?>?>

        @POST("recipe/create")
        fun createRecipe(
            @Body recipeDetail: RecipeDetail? //                @Field("user_id") String userID,
            //                                                  @Field("recipe_name") String recipeName,
            //                                                  @Field("recipe_img") String recipeImg,
            //                                                  @Field("contents") String contents,
            //                                                  @Field("datetime") long datetime,
            //                                                  @Field("amount_time") String amountTime,
            //                                                  @Field("ingredients") ArrayList<Ingredient> ingredients,
            //                                                  @Field("tags") ArrayList<String> tags,
            //                                                  @Field("phase") ArrayList<RecipePhase> phases
        ): Single<Response<JsonObject?>?>?
    }

    interface ReviewService {
        @GET("review/")
        fun getReview(@Query("recipe_id") recipeId: Int): Single<Response<ArrayList<Review?>?>?>

        @FormUrlEncoded
        @POST("review/create")
        fun createReview(
            @Field("recipe_id") recipeID: Int,
            @Field("user_id") userID: String?,
            @Field("contents") contents: String?,
            @Field("rating") rating: Long,
            @Field("datetime") dateTime: Long
        ): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("review/delete")
        fun deleteReview(@Query("recipe_id") recipeId: Int): Single<Response<JsonObject?>?>?
    }

    interface PostService {
        @get:GET("post/")
        val postList: Single<Response<ArrayList<Post?>?>?>

        @GET("post/")
        fun getPostListByUserID(@Query("user_id") userID: String?): Single<Response<ArrayList<Post?>?>?>

        @GET("post/")
        fun getPostListByNickname(@Query("nickname") nickname: String?): Single<Response<ArrayList<Post?>?>?>?

        @GET("post/detail")
        fun getPost(@Query("post_id") postID: Int): Single<Response<Post?>?>

        @FormUrlEncoded
        @POST("post/like")
        fun setLikePost(
            @Field("user_id") userID: String?,
            @Field("post_id") postID: Int,
            @Field("like") like: Int
        ): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("post/create")
        fun createPost(
            @Field("user_id") userID: String?,
            @Field("post_img") postImgs: String?,
            @Field("contents") contents: String?,
            @Field("datetime") datetime: Long,
            @Field("tags") tags: ArrayList<String>?
        ): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("post/update")
        fun updatePost(
            @Field("post_id") postID: Int,
            @Field("post_img") postImgs: String?,
            @Field("contents") contents: String?,
            @Field("datetime") datetime: Long,
            @Field("tags") tags: ArrayList<String>?
        ): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("post/delete")
        fun deletePost(@Field("post_id") postID: Int): Single<Response<JsonObject?>?>
    }

    interface CommentService {
        @GET("comment/")
        fun getComment(@Query("post_id") postID: Int): Single<Response<ArrayList<Comment?>?>?>

        @FormUrlEncoded
        @POST("comment/create")
        fun createComment(
            @Field("post_id") postID: Int,
            @Field("user_id") userID: String?,
            @Field("contents") contents: String?,
            @Field("datetime") dateTime: Long
        ): Single<Response<JsonObject?>?>

        @FormUrlEncoded
        @POST("comment/delete")
        fun deleteComment(@Field("comment_id") commentID: Int): Single<Response<JsonObject?>?>
    }
}