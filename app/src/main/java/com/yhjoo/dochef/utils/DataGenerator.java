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
import java.util.Arrays;
import java.util.Random;


public class DataGenerator {
    public static <T> T make(Resources resources, int type) {
        int[] img_recipes = {R.raw.dummy_recipe_0, R.raw.dummy_recipe_1,
                R.raw.dummy_recipe_2, R.raw.dummy_recipe_3, R.raw.dummy_recipe_4,
                R.raw.dummy_recipe_5, R.raw.dummy_recipe_6, R.raw.dummy_recipe_7,
                R.raw.dummy_recipe_8, R.raw.dummy_recipe_9};
        int[] img_profiles = {R.raw.dummy_profile_0, R.raw.dummy_profile_1,
                R.raw.dummy_profile_2, R.raw.dummy_profile_3, R.raw.dummy_profile_4,
                R.raw.dummy_profile_5, R.raw.dummy_profile_6, R.raw.dummy_profile_7,
                R.raw.dummy_profile_8, R.raw.dummy_profile_9};
        int[] img_posts = {R.raw.dummy_post_0, R.raw.dummy_post_1,
                R.raw.dummy_post_2, R.raw.dummy_post_3, R.raw.dummy_post_4,
                R.raw.dummy_post_5, R.raw.dummy_post_6, R.raw.dummy_post_7,
                R.raw.dummy_post_8, R.raw.dummy_post_9};

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

                arrayList.add(new Notification(i, resources.getInteger(R.integer.NOTIFICATION_TYPE_1),
                        "recipe_detail",
                        "1",
                        "팔로워 XXX이 새 레시피가 등록되었습니다.",
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        System.currentTimeMillis(),
                        r.nextInt(2)));

                arrayList.add(new Notification(i, resources.getInteger(R.integer.NOTIFICATION_TYPE_2),
                        "recipe_detail",
                        "1",
                        "XXX에 새 리뷰가 등록되었습니다.",
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        System.currentTimeMillis() - i * 1000000,
                        r.nextInt(2)));

                arrayList.add(new Notification(i, resources.getInteger(R.integer.NOTIFICATION_TYPE_3),
                        "post",
                        "1",
                        "XXX에 댓글이 등록되었습니다.",
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        System.currentTimeMillis() - i * 2000000,
                        r.nextInt(2)));

                arrayList.add(new Notification(i, resources.getInteger(R.integer.NOTIFICATION_TYPE_4),
                        "home",
                        "1",
                        "XXX가 당신을 팔로우합니다.",
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        System.currentTimeMillis() - i * 3000000,
                        r.nextInt(2)));
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF)) {
            ArrayList<UserBrief> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                ArrayList<String> follow = new ArrayList<>();

                arrayList.add(new UserBrief("userID",
                        Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "nickname",
                        follow, 1));
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
                        i + "분", i, r.nextInt(6), ingredients, tags
                );

                arrayList.add(recipe);
            }

            return (T) arrayList;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL)) {
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient("얇은 사각어묵", "6장"));
            ingredients.add(new Ingredient("감자", "1개"));
            ingredients.add(new Ingredient("물", "2/3컵"));
            ingredients.add(new Ingredient("양파", "1/2개"));
            ingredients.add(new Ingredient("봉어묵", "6개"));
            ingredients.add(new Ingredient("대파", "1/2대"));
            ingredients.add(new Ingredient("진간장", "5큰술"));
            ingredients.add(new Ingredient("청양고추", "2개"));
            ingredients.add(new Ingredient("간마늘", "1큰술"));
            ingredients.add(new Ingredient("식용유", "2큰술"));
            ingredients.add(new Ingredient("참기름", "2큰술"));
            ingredients.add(new Ingredient("황설탕", "1큰술"));
            ingredients.add(new Ingredient("고운고춧가루", "3/2큰술"));
            ingredients.add(new Ingredient("통깨", "약간"));

            ArrayList<String> tips = new ArrayList<>();
            tips.add("팁 1");
            tips.add("팁 2");
            tips.add("팁 2");
            tips.add("팁 2");
            tips.add("팁 2");
            tips.add("팁 2");
            tips.add("팁 2");

            ArrayList<RecipePhase> phases = new ArrayList<>();
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_0),
                    "얇은 사각어묵은 길게 2등분 후 두께 2cm로 얇게 썰고, 봉어묵은 1cm정도 두께로 어슷 썰어 준비한다.",
                    new ArrayList<>(Arrays.asList()),
                    "5분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("얇은 사각 어묵", "6장"),
                            new Ingredient("봉어묵", "6개")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_1),
                    "양파는 가로로 반 갈라 2cm정도 두께로 자른다.",
                    new ArrayList<>(Arrays.asList(
                            "양파는 더 넣으셔도 돼요."
                    )),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("양파", "1/2개")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_2),
                    "대파, 청양고추는 0.3cm두께로 송송 썬다.",
                    new ArrayList<>(Arrays.asList(
                            "청양고추는 매우면 넣지 마세요."
                    )),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("대파", "1/2대"),
                            new Ingredient("청양고추", "2개")
                            ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_3),
                    "감자는 십자모양으로 4등분 한 후 0.3cm 두께로 편 썬다.",
                    new ArrayList<>(Arrays.asList()),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("감자", "1개")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_4),
                    "깊은 프라이팬에 어묵, 양파, 대파, 청양고추, 감자, 식용유, 간마늘을 넣고 강 불에 올린다.",
                    new ArrayList<>(Arrays.asList()),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("손질한 재료들", "모두"),
                            new Ingredient("간마늘", "1큰술"),
                            new Ingredient("식용유", "2큰술")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_5),
                    "지글지글 소리가 나기 시작하면 전체적으로 섞일 때 까지 볶은 후 물, 진간장,  황설탕을 넣고 졸인다.",
                    new ArrayList<>(Arrays.asList(
                            "어묵의 종류나 불 세기에 따라 물양을 조절한다."
                    )),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("물", "2/3컵"),
                            new Ingredient("진간장", "5큰술"),
                            new Ingredient("황설탕", "1큰술")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_6),
                    " 어묵에 양념장이 배이면 고운고춧가루를 넣고 섞는다.",
                    new ArrayList<>(Arrays.asList()),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("고운고춧가루", "3/2큰술")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_7),
                    "국물이 완전히 졸아들고 윤기가 나면 참기름을 넣고 섞는다.",
                    new ArrayList<>(Arrays.asList()),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("참기름", "2큰술")
                    ))
            ));
            phases.add(new RecipePhase(
                    Integer.toString(R.raw.playrecipe_8),
                    "통깨를 뿌린 후 접시에 옮겨 담아 완성한다.",
                    new ArrayList<>(Arrays.asList(
                            "그릇에 담고나서 통깨를 한번 더 뿌려줘도 좋다."
                    )),
                    "3분",
                    new ArrayList<>(Arrays.asList(
                            new Ingredient("통깨", "약간")
                    ))
            ));

            ArrayList<String> tags = new ArrayList<>();
            tags.add("백종원");
            tags.add("어묵");
            tags.add("반찬");

            ArrayList<String> likes = new ArrayList<>();
            likes.add("유저 1");
            likes.add("유저 2");

            RecipeDetail recipeDetail = new RecipeDetail(
                    1, "백종원 어묵볶음", "userID", "Chef",
                    Integer.toString(img_profiles[0]),
                    Integer.toString(R.raw.playrecipe_start),
                    "반찬 걱정 덜어줄 어묵볶음!\n" +
                            "바로 해서 먹으면 일품요리로도 손색없어요",
                    System.currentTimeMillis() - (1000 * 1000),
                    "30분", 1, likes, 5,
                    ingredients, tags, phases
            );

            return (T) recipeDetail;
        } else if (type == resources.getInteger(R.integer.DATA_TYPE_REVIEW)) {
            ArrayList<Review> arrayList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Random r = new Random();
                arrayList.add(new Review(i, i, "userID",
                        "유저 " + i, Integer.toString(img_profiles[r.nextInt(img_profiles.length)]),
                        "내용 " + i, r.nextInt(6),
                        System.currentTimeMillis() - (1000 * 1000 * i)));
            }

            return (T) arrayList;
        } else {
            Utils.log("no type");
            return (T) new Object();
        }

    }
}