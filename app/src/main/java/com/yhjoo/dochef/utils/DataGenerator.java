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
        int[] img_recipes = {R.raw.dummy_recipe_0, R.raw.dummy_recipe_1,
                R.raw.dummy_recipe_2,R.raw.dummy_recipe_3, R.raw.dummy_recipe_4,
                R.raw.dummy_recipe_5,R.raw.dummy_recipe_6, R.raw.dummy_recipe_7,
                R.raw.dummy_recipe_8,R.raw.dummy_recipe_9};
        int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1,
                R.raw.dummy_profile_2,R.raw.dummy_profile_3,R.raw.dummy_profile_4,
                R.raw.dummy_profile_5,R.raw.dummy_profile_6,R.raw.dummy_profile_7,
                R.raw.dummy_profile_8,R.raw.dummy_profile_9};
        int[] img_posts = {R.raw.dummy_post_0, R.raw.dummy_post_1,
                R.raw.dummy_post_2,R.raw.dummy_post_3,R.raw.dummy_post_4,
                R.raw.dummy_post_5,R.raw.dummy_post_6,R.raw.dummy_post_7,
                R.raw.dummy_post_8,R.raw.dummy_post_9};

        if (type == resources.getInteger(R.integer.DATA_TYPE_FAQ)) {
            ArrayList<FAQ> arrayList = new ArrayList<>();

            for (int i = 1; i < 10; i++) {
                arrayList.add(new FAQ("FAQ Title " + i, "F\nA\nQ\n" + i));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_NOTICE)) {
            ArrayList<Notice> arrayList = new ArrayList<>();


            for (int i = 1; i < 10; i++) {
                arrayList.add(new Notice("Notice Title " + i, "No\nti\nce\n" + i, System.currentTimeMillis()));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION)) {

            ArrayList<Notification> arrayList = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                Random r = new Random();

                arrayList.add(new Notification(resources.getInteger(R.integer.NOTIFICATION_TYPE_1),
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "유저 " + i,
                        null,
                        "12:00"));
                arrayList.add(new Notification(resources.getInteger(R.integer.NOTIFICATION_TYPE_2),
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        null,
                        "레시피 " + i,
                        "8시간 전"));
                arrayList.add(new Notification(resources.getInteger(R.integer.NOTIFICATION_TYPE_3),
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "yy",
                        "레시피 " + i,
                        "4월 14일"));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.LOCAL_TYPE_USER_BRIEF)) {
            ArrayList<UserBrief> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                ArrayList<String> follow = new ArrayList<>();

                arrayList.add(new UserBrief("userID",
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "nickname",
                        follow,1));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL)) {
            Random r = new Random();
            ArrayList<String> follow = new ArrayList<>();
            follow.add("유저1");
            UserDetail userDetail = new UserDetail(
                    "userID",
                    Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                    "유저",
                    "유저 소개글",
                    1,
                    follow,
                    1,
                    1
            );

            return (T) userDetail;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_POST)) {
            ArrayList<Post> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();

                ArrayList<String> tags = new ArrayList<>();
                tags.add("태그" + i);
                tags.add("태그" + i);
                ArrayList<String> likes = new ArrayList<>();
                likes.add("유저" + i);
                ArrayList<Comment> comments = new ArrayList<>();
                comments.add(new Comment(i, i, "userID" + i,
                        "유저 ", Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "내용 ", System.currentTimeMillis() - (1000 * 1000 * i)
                ));

                arrayList.add(new Post(i,
                        "userID",
                        "유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        Integer.toString(img_posts[r.nextInt(img_posts.length)]),
                        System.currentTimeMillis() - (1000 * 1000 * i),
                        "내용 " + i,
                        tags,
                        comments,
                        likes)
                );
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_COMMENTS)) {
            ArrayList<Comment> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                arrayList.add(new Comment(i,
                        i,
                        "userID" + i,
                        "유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "내용 " + i,
                        System.currentTimeMillis() - (1000 * 1000 * i)
                ));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATE_TYPE_RECIPE)) {
            ArrayList<Recipe> arrayList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Random r = new Random();
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                Ingredient ingredient = new Ingredient("재료" + i, i + "스푼");
                ingredients.add(ingredient);
                ArrayList<String> tags = new ArrayList<>();
                tags.add("태그 1");
                tags.add("태그 2");

                Recipe recipe = new Recipe(i, "레시피 " + i, "userID",
                        "유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        Integer.toString(img_recipes[r.nextInt(img_recipes.length)]),
                        "내용 " + i, System.currentTimeMillis() - (1000 * 1000 * i),
                        i + "분", i, r.nextInt(6),ingredients, tags
                );

                arrayList.add(recipe);
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL)) {
            Random r = new Random();
            ArrayList<String> tags = new ArrayList<>();
            tags.add("태그 1");
            tags.add("태그 2");

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            Ingredient ingredient = new Ingredient("재료" + 1, 1 + "스푼");
            ingredients.add(ingredient);
            ingredients.add(ingredient);

            ArrayList<String> likes = new ArrayList<>();
            likes.add("유저 1");
            likes.add("유저 2");

            ArrayList<RecipePhase> phases = new ArrayList<>();
            RecipePhase phase = new RecipePhase(

            );
            phases.add(phase);

            RecipeDetail recipeDetail = new RecipeDetail(
                    1, "레시피 " + 1, "userID", "유저 " + 1,
                    Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                    Integer.toString(img_recipes[r.nextInt(img_recipes.length)]),
                    "내용 " + 1, System.currentTimeMillis() - (1000 * 1000),
                    1 + "분", 1, likes ,r.nextInt(6),
                    ingredients, tags, phases
            );

            return (T) recipeDetail;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_REVIEW)) {
            ArrayList<Review> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                arrayList.add(new Review(i, i, "userID",
                        "유저 " + i, Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "내용 " + i, r.nextInt(6),
                        System.currentTimeMillis() - (1000 * 1000 * i)));
            }

            return (T) arrayList;
        }
        return (T) new Object();
    }
}