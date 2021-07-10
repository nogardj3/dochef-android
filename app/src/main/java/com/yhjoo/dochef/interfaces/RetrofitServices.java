package com.yhjoo.dochef.interfaces;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.model.Notice;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipeDetailPlay;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.model.UserBreif;
import com.yhjoo.dochef.model.UserDetail;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        Call<JsonObject> checkUser(@Field("user_token") String token, @Field("user_id") String uid);
        @FormUrlEncoded
        @POST("user/signup")
        Call<JsonObject> createUser(@Field("user_token") String token, @Field("user_id") String uid, @Field("nickname") String nickname);
    }

    public interface UserService {
        @GET("user/follower")
        Call<List<UserBreif>> getFollowers(@Query("user_id") String user_id,@Query("target_id") String target_id);
        @GET("user/following")
        Call<List<UserBreif>> getFollowings(@Query("user_id") String user_id,@Query("target_id") String target_id);
        @FormUrlEncoded
        @POST("user/subscribe")
        Call<List<UserBreif>> subscribeUser(@Query("user_id") String user_id,@Query("target_id") String target_id);
        @FormUrlEncoded
        @POST("user/unsubscribe")
        Call<List<UserBreif>> unsubscribeUser(@Query("user_id") String user_id,@Query("target_id") String target_id);
        @GET("user/detail")
        Call<List<UserDetail>> getUserDetail(@Query("user_id") String user_id);
    }

    public interface RecipeService {
        @GET("recipe/detail")
        Call<RecipeDetailPlay> getRecipeDetail(@Field("recipe_id") int recipeId);
        @GET("recipe/default")
        Call<RecipeDetail> getRecipe(@Field("recipe_id") int recipeId);
    }

    public interface ReviewService {
        @GET("review/")
        Call<ArrayList<Review>> getReview(@Query("recipe_id") int recipeId);
        @GET("comment/")
        Call<ArrayList<Review>> createReview(@Query("recipe_id") int recipeId);
    }

    public interface PostService {
        @GET("post/")
        Call<ArrayList<Post>> getPost(@Query("user_id") String userID);
        @GET("post/detail")
        Call<ArrayList<Post>> getPostDetail(@Query("user_id") String userID);
        @FormUrlEncoded
        @POST("post/create")
        Call<ArrayList<Post>> createPost(@Query("post_id") String userID);
        @FormUrlEncoded
        @POST("post/delete")
        Call<ArrayList<Post>> deletePost(@Query("post_id") String userID);
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



    //-------------USERINFO
    public interface MyHomeService {
        @GET("user/info/home.php")
        Call<UserDetail> GetBasicInfoCall(@Query("User_ID") String id);
    }

    public interface SearchUserService {
        @GET("search/user.php")
        Call<List<UserBreif>> SearchUserCall(@Query("keyword") String keyword, @Query("last") int last);
    }

    //-------------POST

    public interface PostActivityService {
        @GET("post/commentlist.php")
        Call<ArrayList<Comment>> GetCommentCall(@Query("PostID") int postID);
    }

    public interface TimeLineService {
        @GET("post/timeline.php")
        Call<ArrayList<Post>> GetPostCall(@Query("last") int last);
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
