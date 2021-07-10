package com.yhjoo.dochef.utils;

import android.content.res.Resources;

import com.yhjoo.dochef.R;
import com.yhjoo.dochef.model.Comment;
import com.yhjoo.dochef.model.FAQ;
import com.yhjoo.dochef.model.Notice;
import com.yhjoo.dochef.model.Notification;
import com.yhjoo.dochef.model.Recipe;
import com.yhjoo.dochef.model.RecipeThumbnail;
import com.yhjoo.dochef.model.Review;
import com.yhjoo.dochef.model.UserBreif;

import java.util.ArrayList;
import java.util.Random;


public class DummyMaker {
    public static <T> T make(Resources resources, int type) {
        if (type == resources.getInteger(R.integer.DUMMY_TYPE_PROFILE)) {
            ArrayList<UserBreif> arrayList = new ArrayList<>();
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            for (int i = 0; i < 5; i++) {
                Random r = new Random();

                arrayList.add(new UserBreif("유저 " + i,
                        Integer.toString(img_profiles[r.nextInt(3)]),
                        "더미 유저 " + i, 0));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_GRID)) {
            ArrayList<RecipeThumbnail> arrayList = new ArrayList<>();

            int type_recipe = resources.getInteger(R.integer.HOMEITEM_TYPE_RECIPE);
            int type_photo = resources.getInteger(R.integer.HOMEITEM_TYPE_PHOTO);
            int[] img_grids = {R.raw.dummy_grid_0, R.raw.dummy_grid_1, R.raw.dummy_grid_2};

            arrayList.add(new RecipeThumbnail(type_recipe, Integer.toString(img_grids[0]), 1));
            for (int i = 0; i < 10; i++) {
                Random r = new Random();

                arrayList.add(new RecipeThumbnail(type_recipe,
                        Integer.toString(img_grids[r.nextInt(3)]),
                        i == 0 ? 1 : 0));
                arrayList.add(new RecipeThumbnail(type_photo,
                        Integer.toString(img_grids[r.nextInt(3)]),
                        i == 0 ? 1 : 0));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_RECIPIES)) {
            ArrayList<Recipe> arrayList = new ArrayList<>();

            int[] img_recipies = {R.raw.dummy_recipe_0, R.raw.dummy_recipe_1, R.raw.dummy_recipe_2};

            ArrayList<String> ingredients = new ArrayList<>();
            ingredients.add("재료1");
            ingredients.add("재료2");
            ArrayList<String> tags = new ArrayList<>();
            ingredients.add("태그1");
            ingredients.add("태그2");

            for (int i = 1; i < 20; i++) {
                Random r = new Random();

                arrayList.add(new Recipe("요리" + i,
                        "요리사" + i,
                        "메세지" + i,
                        r.nextInt(1000),
                        Integer.toString(img_recipies[r.nextInt(3)]),
                        ingredients,
                        tags));
                arrayList.add(new Recipe("요리" + (i + 1),
                        "나",
                        "메세지" + (i + 1),
                        r.nextInt(1000),
                        Integer.toString(img_recipies[r.nextInt(3)]),
                        ingredients,
                        tags));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_GRID)) {
            ArrayList<Comment> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                arrayList.add(new Comment("유져" + i, "내용 " + i + "\n", System.currentTimeMillis()));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_FAQ)) {
            ArrayList<FAQ> arrayList = new ArrayList<>();

            for (int i = 1; i < 10; i++) {
                arrayList.add(new FAQ("자주묻는 질문" + i, "자\n주\n묻\n는\n질\n문"));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_NOTICE)) {
            ArrayList<Notice> arrayList = new ArrayList<>();


            for (int i = 1; i < 10; i++) {
                arrayList.add(new Notice("공지사항 " + i, "공\n지\n사\n항", System.currentTimeMillis()));
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
        } else if (type == resources.getInteger(R.integer.DUMMY_TYPE_REVIEW)) {
            ArrayList<Review> arrayList = new ArrayList<>();

            int[] img_recipies = {R.raw.dummy_recipe_0, R.raw.dummy_recipe_1, R.raw.dummy_recipe_2};
            int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1, R.raw.dummy_profile_2};

            Random r = new Random();
            arrayList.add(new Review(Integer.toString(img_recipies[r.nextInt(3)]), "userid", Integer.toString(img_profiles[r.nextInt(3)]), "유저0", "내용", 0, 3));
            arrayList.add(new Review(Integer.toString(img_recipies[r.nextInt(3)]), "userid", Integer.toString(img_profiles[r.nextInt(3)]), "유저1", "내용", 0, 5));

            return (T) arrayList;
        }
        return (T) new Object();
    }
}