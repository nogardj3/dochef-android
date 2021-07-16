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

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class RetrofitServices {
    public interface BasicService {
        @GET("notice")
        Call<ArrayList<Notice>> getNotice();

        @GET("faq")
        Call<ArrayList<FAQ>> getFAQ();

        @GET("tos")
        Call<String> getTOS();
    }

    public interface AccountService {
        @FormUrlEncoded
        @POST("user/check/")
        Call<UserBrief> checkUser(@Field("user_token") String token,
                                  @Field("user_id") String uid);

        @FormUrlEncoded
        @POST("user/signup")
        Call<UserBrief> createUser(@Field("user_token") String token,
                                   @Field("user_id") String uid,
                                   @Field("nickname") String nickname);
    }

    public interface UserService {
        @GET("user/")
        Call<ArrayList<UserBrief>> getUserByNickname(@Query("nickname") String nickname);

        @GET("user/detail")
        Call<UserDetail> getUserDetail(@Query("user_id") String user_id);

        @GET("user/follower")
        Call<ArrayList<UserBrief>> getFollowers(@Query("target_id") String target_id);

        @GET("user/following")
        Call<ArrayList<UserBrief>> getFollowings(@Query("target_id") String target_id);

        @FormUrlEncoded
        @POST("user/subscribe")
        Call<JsonObject> subscribeUser(@Field("user_id") String user_id,
                                                 @Field("target_id") String target_id);

        @FormUrlEncoded
        @POST("user/unsubscribe")
        Call<JsonObject> unsubscribeUser(@Field("user_id") String user_id,
                                                   @Field("target_id") String target_id);
    }

    public interface RecipeService {
        @GET("recipe/detail")
        Call<RecipeDetail> getRecipeDetail(@Query("recipe_id") int recipeId);


        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipes(@Query("sort") String sort);

        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeByUserID(@Query("user_id") String userId, @Query("sort") String sort);

        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeByName(@Query("recipe_name") String recipeName, @Query("sort") String sort);

        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeByTag(@Query("tag") String tag, @Query("sort") String sort);

        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeByIngredient(@Query("ingredient") String ingredient, @Query("sort") String sort);

        @FormUrlEncoded
        @POST("recipe/count")
        Call<JsonObject> addCount(@Field("recipe_id") int recipeId);

        @FormUrlEncoded
        @POST("recipe/like")
        Call<JsonObject> setLikeRecipe(@Field("recipe_id") int recipeId, @Field("user_id") String userId , @Field("like") int like);

        @FormUrlEncoded
        @POST("review/create")
        Call<JsonObject> createRecipe(@Field("user_id") String userID,
                                      @Field("recipe_name") String recipeName,
                                      @Field("recipe_img") String recipeImg,
                                      @Field("contents") String contents,
                                      @Field("datetime") long datetime,
                                      @Field("amount_time") String amountTime,
                                      @Field("ingredients") Ingredient[] ingredients,
                                      @Field("tags") String[] tags,
                                      @Field("phase") RecipePhase[] phases);

        @FormUrlEncoded
        @POST("review/delete")
        Call<JsonObject> deleteRecipe(@Field("recipe_id") int recipeId);
    }

    public interface ReviewService {
        @GET("review/")
        Call<ArrayList<Review>> getReview(@Query("recipe_id") int recipeId);

        @GET("review/create")
        Call<JsonObject> createReview(@Query("recipe_id") int recipeId);

        @GET("review/delete")
        Call<JsonObject> deleteReview(@Query("recipe_id") int recipeId);
    }

    public interface PostService {
        @GET("post/")
        Call<ArrayList<Post>> getPostList();

        @GET("post/")
        Call<ArrayList<Post>> getPostListByUserID(@Query("user_id") String userID);

        @GET("post/")
        Call<ArrayList<Post>> getPostListByNickname(@Query("nickname") String nickname);

        @GET("post/detail")
        Call<Post> getPost(@Query("post_id") int postID);

        @FormUrlEncoded
        @POST("post/like")
        Call<JsonObject> setLikePost(@Field("user_id") String userID,
                                     @Field("post_id") int postID,
                                     @Field("like") int like);

        @FormUrlEncoded
        @POST("post/create")
        Call<JsonObject> createPost(@Field("user_id") String userID,
                                    @Field("post_img") String postImgs,
                                    @Field("contents") String contents,
                                    @Field("datetime") long datetime,
                                    @Field("tags") ArrayList<String> tags);

        @FormUrlEncoded
        @POST("post/update")
        Call<JsonObject> updatePost(@Field("post_id") int postID,
                                    @Field("post_img") String postImgs,
                                    @Field("contents") String contents,
                                    @Field("datetime") long datetime,
                                    @Field("tags") ArrayList<String> tags);

        @FormUrlEncoded
        @POST("post/delete")
        Call<JsonObject> deletePost(@Field("post_id") int postID);
    }

    public interface CommentService {
        @GET("comment/")
        Call<ArrayList<Comment>> getComment(@Query("post_id") int postID);

        @FormUrlEncoded
        @POST("comment/create")
        Call<JsonObject> createComment(@Field("post_id") int postID,
                                       @Field("user_id") String userID,
                                       @Field("contents") String contents,
                                       @Field("datetime") long dateTime);

        @FormUrlEncoded
        @POST("comment/delete")
        Call<JsonObject> deleteComment(@Field("comment_id") int commentID);
    }
}