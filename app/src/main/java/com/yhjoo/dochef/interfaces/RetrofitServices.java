package com.yhjoo.dochef.interfaces;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.classes.Comment;
import com.yhjoo.dochef.classes.FAQ;
import com.yhjoo.dochef.classes.Notice;
import com.yhjoo.dochef.classes.Post;
import com.yhjoo.dochef.classes.PostComment;
import com.yhjoo.dochef.classes.Recipe;
import com.yhjoo.dochef.classes.RecipeDetail;
import com.yhjoo.dochef.classes.RecipeDetailPlay;
import com.yhjoo.dochef.classes.Review;
import com.yhjoo.dochef.classes.UserDetail;
import com.yhjoo.dochef.classes.UserList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public class RetrofitServices {
    public interface BasicService {
        @GET("notice")
        Call<ArrayList<Notice>> getNotice();
        @GET("faq")
        Call<ArrayList<FAQ>> getFAQ();
        @GET("tos")
        Call<String> getTOS();
    }

    public interface RecipeService {
        @GET("recipe/detail")
        Call<RecipeDetailPlay> getRecipeDetail(@Field("recipe_id") int recipeId);
        @GET("recipe/default")
        Call<RecipeDetail> getRecipe(@Field("recipe_id") int recipeId);
        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeListByNickname(@Field("nickname") String nickname);
        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeListByName(@Field("recipe_name") int recipeName);
        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeListByTag(@Field("tags") int tags);
        @GET("recipe/")
        Call<ArrayList<Recipe>> getRecipeListByIngredient(@Field("ingredients") int ingredients);
    }

    public interface ReviewService {
        @GET("review/")
        Call<ArrayList<Review>> getReview(@Query("recipe_id") int recipeId);
    }

    public interface AccountService {
        @FormUrlEncoded
        @POST("user/check/token")
        Call<JsonObject> checkToken(@Field("token") String token);
        @FormUrlEncoded
        @POST("user/check/nickname")
        Call<JsonObject> checkNickname(@Field("nickname") String nickname);
        @FormUrlEncoded
        @POST("user/signup/")
        Call<JsonObject> signUp(@Field("token") String token, @Field("nickname") String nickname);
    }

    public interface UserService {
        @GET("user/follower")
        Call<List<UserList>> getFollowers(@Query("nickname") String nickname, @Query("index") int index);
        @GET("user/following")
        Call<List<UserList>> getFollowings(@Query("nickname") String nickname, @Query("index") int index);
        @GET("user/detail")
        Call<List<UserDetail>> getUserDetail(@Query("nickname") String nickname);
        @GET("user/")
        Call<List<UserList>> getUserLists(@Query("nickname") String nickname);
    }

    public interface PostService {
        @GET("post/timeline.php")
        Call<ArrayList<Post>> getPost(@Query("last") int last);
    }

    public interface CommentService {
        @GET("comment/")
        Call<ArrayList<PostComment>> getComment(@Query("post_id") int postId);
    }


    //-------------USERINFO
    public interface FollowerService {
        @GET("user/follow/followerlist.php")
        Call<List<UserList>> GetFollowerCall(@Query("UserID") String userID, @Query("last") int last);
    }

    public interface FollowingService {
        @GET("user/follow/followinglist.php")
        Call<List<UserList>> GetFollowingCall(@Query("UserID") String userID, @Query("last") int last);
    }

    public interface MyHomeService {
        @GET("user/info/home.php")
        Call<UserDetail> GetBasicInfoCall(@Query("User_ID") String id);
    }

    public interface UserHomeService {
        @GET("user/info/home.php")
        Call<UserDetail> GetBasicInfoCall(@QueryMap Map<String, String> option);

        @FormUrlEncoded
        @POST("user/follow/follow.php")
        Call<JSONObject> FollowCall(@Field("User_ID") String userID, @Field("follow") int follow);
    }

    public interface SearchUserService {
        @GET("search/user.php")
        Call<List<UserList>> SearchUserCall(@Query("keyword") String keyword, @Query("last") int last);
    }

    //-------------ACCOUNT

    public interface LoginService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
    }


    //-------------POST

    public interface PostActivityService {
        @GET("post/commentlist.php")
        Call<ArrayList<PostComment>> GetCommentCall(@Query("PostID") int postID);
    }

    public interface SignUpService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
        @FormUrlEncoded
        @POST("user/sign/signup.php")
        Call<JsonObject> SignupCall(@Field("token") String token, @Field("Nickname") String Nickname);
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
