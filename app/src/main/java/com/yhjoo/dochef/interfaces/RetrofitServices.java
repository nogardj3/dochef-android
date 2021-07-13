package com.yhjoo.dochef.interfaces;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.model.Notice;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePlay;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.model.UserBreif;
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
        Call<UserBreif> checkUser(@Field("user_token") String token, @Field("user_id") String uid);

        @FormUrlEncoded
        @POST("user/signup")
        Call<UserBreif> createUser(@Field("user_token") String token, @Field("user_id") String uid, @Field("nickname") String nickname);
    }

    public interface UserService {
        @GET("user/")
        Call<ArrayList<UserBreif>> getUserByNickname(@Query("nickname") String nickname);

        @GET("user/detail")
        Call<ArrayList<UserDetail>> getUserDetail(@Query("user_id") String user_id);

        @GET("user/follower")
        Call<ArrayList<UserBreif>> getFollowers(@Query("user_id") String user_id, @Query("target_id") String target_id);

        @GET("user/following")
        Call<ArrayList<UserBreif>> getFollowings(@Query("user_id") String user_id, @Query("target_id") String target_id);

        @FormUrlEncoded
        @POST("user/subscribe")
        Call<ArrayList<UserBreif>> subscribeUser(@Query("user_id") String user_id, @Query("target_id") String target_id);

        @FormUrlEncoded
        @POST("user/unsubscribe")
        Call<ArrayList<UserBreif>> unsubscribeUser(@Query("user_id") String user_id, @Query("target_id") String target_id);
    }

    public interface RecipeService {
        @GET("recipe/detail")
        Call<RecipePlay> getRecipeDetail(@Field("recipe_id") int recipeId);

        @GET("recipe/default")
        Call<RecipeDetail> getRecipe(@Field("recipe_id") int recipeId);
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
        Call<JsonObject> likePost(@Field("user_id") String userID, @Field("post_id") int postID, @Field("like") int like);

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

        @POST("comment/create")
        Call<JsonObject> createComment(@Field("post_id") int postID,
                                       @Field("user_id") String userID,
                                       @Field("contents") String contents,
                                       @Field("datetime") long dateTime);

        @POST("comment/delete")
        Call<JsonObject> deleteComment(@Field("comment_id") int commentID);
    }

    //-------------RECIPE
    public interface OverViewService {
        @FormUrlEncoded
        @POST("recipe/overview.php")
        Call<RecipeDetail> LoadOverViewCall(@Field("recipeID") int id);

        @FormUrlEncoded
        @POST("recipe/loadComment.php")
        Call<ArrayList<Comment>> LoadCommentCall(@Field("recipeID") int id);
    }
}