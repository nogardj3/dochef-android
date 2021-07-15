package com.yhjoo.dochef.utils;

import android.content.res.Resources;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.model.Ingredient;
import com.yhjoo.dochef.model.Notice;
import com.yhjoo.dochef.model.Notification;
import com.yhjoo.dochef.model.Post;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.RecipeDetail;
import com.yhjoo.dochef.model.RecipePhase;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.model.UserBrief;
import com.yhjoo.dochef.model.UserDetail;

import java.util.ArrayList;
import java.util.Random;


public class DataGenerator {
    public static <T> T make(Resources resources, int type) {
        if (type == resources.getInteger(R.integer.DUMMY_TYPE_FAQ)) {
            ArrayList<FAQ> arrayList = new ArrayList<>();

            for (int i = 1; i < 10; i++) {
                arrayList.add(new FAQ("FAQ Title " + i, "F\nA\nQ\n" + i));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_NOTICE)) {
            ArrayList<Notice> arrayList = new ArrayList<>();


            for (int i = 1; i < 10; i++) {
                arrayList.add(new Notice("Notice Title " + i, "No\nti\nce\n" + i, System.currentTimeMillis()));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_NOTIFICATION)) {
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            ArrayList<Notification> arrayList = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                Random r = new Random();

                arrayList.add(new Notification(resources.getInteger(R.integer.NOTIFICATION_TYPE_1),
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        "유저 " + i,
                        null,
                        "12:00"));
                arrayList.add(new Notification(resources.getInteger(R.integer.NOTIFICATION_TYPE_2),
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        null,
                        "레시피 " + i,
                        "8시간 전"));
                arrayList.add(new Notification(resources.getInteger(R.integer.NOTIFICATION_TYPE_3),
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        "yy",
                        "레시피 " + i,
                        "4월 14일"));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_USER_BRIEF)) {
            ArrayList<UserBrief> arrayList = new ArrayList<>();
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            for (int i = 0; i < 10; i++) {
                Random r = new Random();

                arrayList.add(new UserBrief("userID",
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        "유저 " + i, i % 2 == 1 ? 1 : 0));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_USER_DETAIL)) {
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};
            Random r = new Random();


            UserDetail userDetail = new UserDetail(
                    "userID",
                    Integer.toString(img_profiles[r.nextInt(3)]),
                    "유저",
                    "유저 소개글",
                    1,
                    1,
                    1
            );

            return (T) userDetail;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_POST)) {
            ArrayList<Post> arrayList = new ArrayList<>();

            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            for (int i = 0; i < 10; i++) {
                Random r = new Random();

                ArrayList<String> tags = new ArrayList<>();
                tags.add("태그" + i);
                ArrayList<String> likes = new ArrayList<>();
                likes.add("유저" + i);
                ArrayList<Comment> comments = new ArrayList<>();
                comments.add(new Comment(i, i, "userID" + i,
                        "유저 " + i, Integer.toString(img_profiles[r.nextInt(3)]),
                        "내용 " + i, System.currentTimeMillis() - (1000 * 1000 * i)
                ));

                arrayList.add(new Post(i,
                        "userID",
                        "유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        Integer.toString(R.raw.dummy_post),
                        System.currentTimeMillis() - (1000 * 1000 * i),
                        "내용 " + i,
                        tags,
                        comments,
                        likes)
                );
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_COMMENTS)) {
            ArrayList<Comment> arrayList = new ArrayList<>();

            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                arrayList.add(new Comment(i,
                        i,
                        "userID" + i,
                        "유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        "내용 " + i,
                        System.currentTimeMillis() - (1000 * 1000 * i)
                ));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_RECIPE)) {
            ArrayList<Recipe> arrayList = new ArrayList<>();

            int[] img_grids = {R.raw.dummy_grid_0, R.raw.dummy_grid_1, R.raw.dummy_grid_2};
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            for (int i = 0; i < 20; i++) {
                Random r = new Random();
                Ingredient ingredient = new Ingredient("재료" + i, i, "스푼");
                Recipe recipe = new Recipe(i, "레시피 " + i, "userID",
                        "유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        Integer.toString(img_grids[r.nextInt(3)]),
                        "내용 " + i, System.currentTimeMillis() - (1000 * 1000 * i),
                        i + "분", i, r.nextInt(6)
                );

                arrayList.add(recipe);
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_RECIPE_DETAIL)) {
            int[] img_recipe = {R.raw.dummy_recipe_0, R.raw.dummy_recipe_1, R.raw.dummy_recipe_2};
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            Random r = new Random();

            Ingredient ingredient = new Ingredient("재료" + 1, 1, "스푼");
            RecipeDetail recipeDetail = new RecipeDetail(
                    1, "레시피 " + 1, "userID", "유저 " + 1,
                    Integer.toString(img_profiles[r.nextInt(3)]),
                    Integer.toString(img_recipe[r.nextInt(3)]),
                    "내용 " + 1, System.currentTimeMillis() - (1000 * 1000),
                    1 + "분", 1, r.nextInt(6),
                    new String[]{}, new String[]{}, new RecipePhase()
            );

            return (T) recipeDetail;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_REVIEW)) {
            ArrayList<Review> arrayList = new ArrayList<>();

            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                arrayList.add(new Review(i, i, "userID",
                        "유저 " + i, Integer.toString(img_profiles[r.nextInt(3)]),
                        "내용 " + i, r.nextInt(6),
                        System.currentTimeMillis() - (1000 * 1000 * i)));
            }

            return (T) arrayList;
        }
        return (T) new Object();
    }
}