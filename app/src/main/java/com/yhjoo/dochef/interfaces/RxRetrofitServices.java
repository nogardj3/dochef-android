package com.yhjoo.dochef.interfaces;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.Notice;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePhase;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.model.UserDetail;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class RxRetrofitServices {
    public interface BasicService {
        @GET("notice")
        Single<Response<ArrayList<Notice>>> getNotice();

        @GET("faq")
        Single<Response<ArrayList<FAQ>>> getFAQ();

        @GET("tos")
        Single<Response<JsonObject>>  getTOS();

        @GET("alive")
        Single<Response<JsonObject>>  checkAlive();
    }

    public interface AccountService {
        @GET("user/check/nickname")
        Single<Response<JsonObject>> checkNickname(@Query("nickname") String nickname);

        @FormUrlEncoded
        @POST("user/check/")
        Single<Response<UserBrief>> checkUser(@Field("user_token") String token,
                                  @Field("user_id") String uid);

        @FormUrlEncoded
        @POST("user/signup")
        Single<Response<UserBrief>> createUser(@Field("user_token") String token,
                                   @Field("user_id") String uid,
                                   @Field("nickname") String nickname);
    }

    public interface UserService {
        @GET("user/")
        Single<Response<ArrayList<UserBrief>>> getUserByNickname(@Query("nickname") String nickname);

        @GET("user/follower")
        Single<Response<ArrayList<UserBrief>>> getFollowers(@Query("target_id") String target_id);

        @GET("user/detail")
        Single<Response<UserDetail>> getUserDetail(@Query("user_id") String user_id);

        @GET("user/following")
        Single<Response<ArrayList<UserBrief>>> getFollowings(@Query("target_id") String target_id);

        @FormUrlEncoded
        @POST("user/subscribe")
        Single<Response<JsonObject>> subscribeUser(@Field("user_id") String user_id,
                                                 @Field("target_id") String target_id);

        @FormUrlEncoded
        @POST("user/unsubscribe")
        Single<Response<JsonObject>> unsubscribeUser(@Field("user_id") String user_id,
                                                   @Field("target_id") String target_id);
    }

    public interface RecipeService {
        @GET("recipe/detail")
        Single<Response<RecipeDetail>> getRecipeDetail(@Query("recipe_id") int recipeId);


        @GET("recipe/")
        Single<Response<ArrayList<Recipe>>> getRecipes(@Query("sort") String sort);

        @GET("recipe/")
        Single<Response<ArrayList<Recipe>>> getRecipeByUserID(@Query("user_id") String userId, @Query("sort") String sort);

        @GET("recipe/")
        Single<Response<ArrayList<Recipe>>> getRecipeByName(@Query("recipe_name") String recipeName, @Query("sort") String sort);

        @GET("recipe/")
        Single<Response<ArrayList<Recipe>>> getRecipeByTag(@Query("tag") String tag, @Query("sort") String sort);

        @GET("recipe/")
        Single<Response<ArrayList<Recipe>>> getRecipeByIngredient(@Query("ingredient") String ingredient, @Query("sort") String sort);

        @FormUrlEncoded
        @POST("recipe/count")
        Single<Response<JsonObject>> addCount(@Field("recipe_id") int recipeId);

        @FormUrlEncoded
        @POST("recipe/like")
        Single<Response<JsonObject>> setLikeRecipe(@Field("recipe_id") int recipeId, @Field("user_id") String userId , @Field("like") int like);

        @FormUrlEncoded
        @POST("recipe/create")
        Single<Response<JsonObject>> createRecipe(@Field("user_id") String userID,
                                      @Field("recipe_name") String recipeName,
                                      @Field("recipe_img") String recipeImg,
                                      @Field("contents") String contents,
                                      @Field("datetime") long datetime,
                                      @Field("amount_time") String amountTime,
                                      @Field("ingredients") Ingredient[] ingredients,
                                      @Field("tags") String[] tags,
                                      @Field("phase") RecipePhase[] phases);

    }

    public interface ReviewService {
        @GET("review/")
        Single<Response<ArrayList<Review>>> getReview(@Query("recipe_id") int recipeId);

        @GET("review/create")
        Single<Response<JsonObject>> createReview(@Query("recipe_id") int recipeId);

        @GET("review/delete")
        Single<Response<JsonObject>> deleteReview(@Query("recipe_id") int recipeId);
    }

    public interface PostService {
        @GET("post/")
        Single<Response<ArrayList<Post>>> getPostList();

        @GET("post/")
        Single<Response<ArrayList<Post>>> getPostListByUserID(@Query("user_id") String userID);

        @GET("post/")
        Single<Response<ArrayList<Post>>> getPostListByNickname(@Query("nickname") String nickname);

        @GET("post/detail")
        Single<Response<Post>> getPost(@Query("post_id") int postID);

        @FormUrlEncoded
        @POST("post/like")
        Single<Response<JsonObject>> setLikePost(@Field("user_id") String userID,
                                     @Field("post_id") int postID,
                                     @Field("like") int like);

        @FormUrlEncoded
        @POST("post/create")
        Single<Response<JsonObject>> createPost(@Field("user_id") String userID,
                                    @Field("post_img") String postImgs,
                                    @Field("contents") String contents,
                                    @Field("datetime") long datetime,
                                    @Field("tags") ArrayList<String> tags);

        @FormUrlEncoded
        @POST("post/update")
        Single<Response<JsonObject>> updatePost(@Field("post_id") int postID,
                                    @Field("post_img") String postImgs,
                                    @Field("contents") String contents,
                                    @Field("datetime") long datetime,
                                    @Field("tags") ArrayList<String> tags);

        @FormUrlEncoded
        @POST("post/delete")
        Single<Response<JsonObject>> deletePost(@Field("post_id") int postID);
    }

    public interface CommentService {
        @GET("comment/")
        Single<Response<ArrayList<Comment>>> getComment(@Query("post_id") int postID);

        @FormUrlEncoded
        @POST("comment/create")
        Single<Response<JsonObject>> createComment(@Field("post_id") int postID,
                                       @Field("user_id") String userID,
                                       @Field("contents") String contents,
                                       @Field("datetime") long dateTime);

        @FormUrlEncoded
        @POST("comment/delete")
        Single<Response<JsonObject>> deleteComment(@Field("comment_id") int commentID);
    }
}