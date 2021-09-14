package com.yhjoo.dochef.utilities

import com.google.gson.JsonObject
import com.yhjoo.dochef.model.*
import retrofit2.Response
import retrofit2.http.*
import java.util.*

class RetrofitServices {
    interface BasicService {
        @GET("notice")
        suspend fun getNotice(): Response<ArrayList<Notice>>

        @GET("faq")
        suspend fun getFAQ(): Response<ArrayList<FAQ>>

        @GET("tos")
        suspend fun getTOS(): Response<JsonObject>

        @GET("alive")
        suspend fun checkAlive(): Response<JsonObject>
    }

    interface AccountService {
        @POST("user/check/nickname")
        suspend fun checkNickname(@Query("nickname") nickname: String): Response<JsonObject>

        @FormUrlEncoded
        @POST("user/check/")
        suspend fun checkUser(
            @Field("user_token") token: String,
            @Field("user_id") uid: String,
            @Field("user_fcm_token") fcmtoken: String
        ): Response<UserBrief>

        @FormUrlEncoded
        @POST("user/signup")
        suspend fun createUser(
            @Field("user_token") token: String,
            @Field("user_fcm_token") fcmtoken: String,
            @Field("user_id") uid: String,
            @Field("nickname") nickname: String
        ): Response<UserBrief>

        @FormUrlEncoded
        @POST("user/update")
        suspend fun updateUser(
            @Field("user_id") userID: String,
            @Field("user_img") userImg: String,
            @Field("nickname") nickname: String,
            @Field("bio") bio: String
        ): Response<JsonObject>
    }

    interface UserService {
        @GET("user/")
        suspend fun getUserByNickname(@Query("nickname") nickname: String): Response<ArrayList<UserBrief>>

        @GET("user/follower")
        suspend fun getFollowers(@Query("target_id") target_id: String): Response<ArrayList<UserBrief>>

        @GET("user/detail")
        suspend fun getUserDetail(@Query("user_id") user_id: String): Response<UserDetail>

        @GET("user/following")
        suspend fun getFollowings(@Query("target_id") target_id: String): Response<ArrayList<UserBrief>>

        @FormUrlEncoded
        @POST("user/subscribe")
        suspend fun subscribeUser(
            @Field("user_id") user_id: String,
            @Field("target_id") target_id: String
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("user/unsubscribe")
        suspend fun unsubscribeUser(
            @Field("user_id") user_id: String,
            @Field("target_id") target_id: String
        ): Response<JsonObject>
    }

    interface RecipeService {
        @GET("recipe/detail")
        suspend fun getRecipeDetail(@Query("recipe_id") recipeId: Int): Response<RecipeDetail>

        @GET("recipe/")
        suspend fun getRecipes(@Query("sort") sort: String): Response<ArrayList<Recipe>>

        @GET("recipe/")
        suspend fun getRecipeByUserID(
            @Query("user_id") userId: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>>

        @GET("recipe/")
        suspend fun getRecipeByName(
            @Query("recipe_name") recipeName: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>>

        @GET("recipe/")
        suspend fun getRecipeByTag(
            @Query("tag") tag: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>>

        @GET("recipe/")
        suspend fun getRecipeByIngredient(
            @Query("ingredient") ingredient: String,
            @Query("sort") sort: String
        ): Response<ArrayList<Recipe>>

        @FormUrlEncoded
        @POST("recipe/count")
        suspend fun addCount(@Field("recipe_id") recipeId: Int): Response<JsonObject>

        @FormUrlEncoded
        @POST("recipe/like")
        suspend fun setLikeRecipe(
            @Field("recipe_id") recipeId: Int,
            @Field("user_id") userId: String,
            @Field("like") like: Int
        ): Response<JsonObject>

        @POST("recipe/create")
        suspend fun createRecipe(
            @Body recipeDetail: RecipeDetail //                @Field("user_id") String userID,
            //                                                  @Field("recipe_name") String recipeName,
            //                                                  @Field("recipe_img") String recipeImg,
            //                                                  @Field("contents") String contents,
            //                                                  @Field("datetime") long datetime,
            //                                                  @Field("amount_time") String amountTime,
            //                                                  @Field("ingredients") ArrayList<Ingredient> ingredients,
            //                                                  @Field("tags") ArrayList<String> tags,
            //                                                  @Field("phase") ArrayList<RecipePhase> phases
        ): Response<JsonObject>
    }

    interface ReviewService {
        @GET("review/")
        suspend fun getReview(@Query("recipe_id") recipeId: Int): Response<ArrayList<Review>>

        @FormUrlEncoded
        @POST("review/create")
        suspend fun createReview(
            @Field("recipe_id") recipeID: Int,
            @Field("user_id") userID: String,
            @Field("contents") contents: String,
            @Field("rating") rating: Long,
            @Field("datetime") dateTime: Long
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("review/delete")
        suspend fun deleteReview(@Query("recipe_id") recipeId: Int): Response<JsonObject>
    }

    interface PostService {
        @GET("post/")
        suspend fun getPostList(): Response<ArrayList<Post>>

        @GET("post/")
        suspend fun getPostListByUserID(@Query("user_id") userID: String): Response<ArrayList<Post>>

        @GET("post/")
        suspend fun getPostListByNickname(@Query("nickname") nickname: String): Response<ArrayList<Post>>

        @GET("post/detail")
        suspend fun getPost(@Query("post_id") postID: Int): Response<Post>

        @FormUrlEncoded
        @POST("post/like")
        suspend fun setLikePost(
            @Field("user_id") userID: String,
            @Field("post_id") postID: Int,
            @Field("like") like: Int
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("post/create")
        suspend fun createPost(
            @Field("user_id") userID: String,
            @Field("post_img") postImgs: String,
            @Field("contents") contents: String,
            @Field("datetime") datetime: Long,
            @Field("tags") tags: ArrayList<String>
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("post/update")
        suspend fun updatePost(
            @Field("post_id") postID: Int,
            @Field("post_img") postImgs: String,
            @Field("contents") contents: String,
            @Field("datetime") datetime: Long,
            @Field("tags") tags: ArrayList<String>
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("post/delete")
        suspend fun deletePost(@Field("post_id") postID: Int): Response<JsonObject>
    }

    interface CommentService {
        @GET("comment/")
        suspend fun getComment(@Query("post_id") postID: Int): Response<ArrayList<Comment>>

        @FormUrlEncoded
        @POST("comment/create")
        suspend fun createComment(
            @Field("post_id") postID: Int,
            @Field("user_id") userID: String,
            @Field("contents") contents: String,
            @Field("datetime") dateTime: Long
        ): Response<JsonObject>

        @FormUrlEncoded
        @POST("comment/delete")
        suspend fun deleteComment(@Field("comment_id") commentID: Int): Response<JsonObject>
    }
}