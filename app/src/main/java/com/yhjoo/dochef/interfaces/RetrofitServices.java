package com.yhjoo.dochef.interfaces;

import com.google.gson.JsonObject;
import com.yhjoo.dochef.classes.Comment;
import com.yhjoo.dochef.classes.Post;
import com.yhjoo.dochef.classes.PostComment;
import com.yhjoo.dochef.classes.RecipeOverview;
import com.yhjoo.dochef.classes.User;
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
    public interface RecipeService {
        // recipe/
    }

    public interface UserService {
        // user/
    }

    public interface CommentService {
        // comment/
    }

    public interface BasicService {
        // notification/
        // help/

    }

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
        Call<User> GetBasicInfoCall(@Query("User_ID") String id);
    }

    public interface UserHomeService {
        @GET("user/info/home.php")
        Call<User> GetBasicInfoCall(@QueryMap Map<String, String> option);

        @FormUrlEncoded
        @POST("user/follow/follow.php")
        Call<JSONObject> FollowCall(@Field("User_ID") String userID, @Field("follow") int follow);
    }

    public interface PostActivityService {
        @GET("post/commentlist.php")
        Call<ArrayList<PostComment>> GetCommentCall(@Query("PostID") int postID);
    }

    public interface TimeLineService {
        @GET("post/timeline.php")
        Call<ArrayList<Post>> GetPostCall(@Query("last") int last);
    }

    public interface OverViewService {
        @FormUrlEncoded
        @POST("recipe/overview.php")
        Call<RecipeOverview> LoadOverViewCall(@Field("recipeID") int id);

        @FormUrlEncoded
        @POST("recipe/loadComment.php")
        Call<ArrayList<Comment>> LoadCommentCall(@Field("recipeID") int id);
    }

    public interface LoginService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
    }

    public interface SignUpService {
        @FormUrlEncoded
        @POST("user/token/check.php")
        Call<JsonObject> CheckTokenCall(@Field("token") String token);
        @FormUrlEncoded
        @POST("user/sign/signup.php")
        Call<JsonObject> SignupCall(@Field("token") String token, @Field("Nickname") String Nickname);
    }


    public interface SearchUserService {
        @GET("search/user.php")
        Call<List<UserList>> SearchUserCall(@Query("keyword") String keyword, @Query("last") int last);
    }
}
