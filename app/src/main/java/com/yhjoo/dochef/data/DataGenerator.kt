package com.yhjoo.dochef.data

import android.content.res.Resources
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.entity.NotificationEntity
import com.yhjoo.dochef.data.model.*
import com.yhjoo.dochef.utils.OtherUtil
import java.util.*

object DataGenerator {
    fun <T> make(resources: Resources, type: Int): T {
        val recipeImgs = arrayOf(
            R.raw.dummy_recipe_0, R.raw.dummy_recipe_1,
            R.raw.dummy_recipe_2, R.raw.dummy_recipe_3, R.raw.dummy_recipe_4,
            R.raw.dummy_recipe_5, R.raw.dummy_recipe_6, R.raw.dummy_recipe_7,
            R.raw.dummy_recipe_8, R.raw.dummy_recipe_9
        )
        val profileImgs = arrayOf(
            R.raw.dummy_profile_0, R.raw.dummy_profile_1,
            R.raw.dummy_profile_2, R.raw.dummy_profile_3, R.raw.dummy_profile_4,
            R.raw.dummy_profile_5, R.raw.dummy_profile_6, R.raw.dummy_profile_7,
            R.raw.dummy_profile_8, R.raw.dummy_profile_9
        )
        val postImgs = arrayOf(
            R.raw.dummy_post_0, R.raw.dummy_post_1,
            R.raw.dummy_post_2, R.raw.dummy_post_3, R.raw.dummy_post_4,
            R.raw.dummy_post_5, R.raw.dummy_post_6, R.raw.dummy_post_7,
            R.raw.dummy_post_8, R.raw.dummy_post_9
        )

        return when (type) {
            resources.getInteger(R.integer.DATA_TYPE_FAQ) -> {
                val arrayList = ArrayList<ExpandableItem>()
                for (i in 1..9) {
                    arrayList.add(ExpandableItem("FAQ Title $i", "FAQ$i", 0))
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATA_TYPE_NOTICE) -> {
                val arrayList = ArrayList<ExpandableItem>()
                for (i in 1..9) {
                    arrayList.add(
                        ExpandableItem(
                            "Notice Title $i",
                            "Notice$i",
                            System.currentTimeMillis()
                        )
                    )
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION) -> {
                val arrayList = ArrayList<NotificationEntity>()
                for (i in 1..9) {
                    val r = Random()
                    arrayList.add(
                        NotificationEntity(
                            i.toLong(), resources.getInteger(R.integer.NOTIFICATION_TYPE_1),
                            "recipe_detail",
                            "1",
                            "팔로워 XXX이 새 레시피가 등록되었습니다.",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            System.currentTimeMillis(),
                            r.nextInt(2)
                        )
                    )
                    arrayList.add(
                        NotificationEntity(
                            i.toLong(), resources.getInteger(R.integer.NOTIFICATION_TYPE_2),
                            "recipe_detail",
                            "1",
                            "XXX에 새 리뷰가 등록되었습니다.",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            System.currentTimeMillis() - i * 1000000,
                            r.nextInt(2)
                        )
                    )
                    arrayList.add(
                        NotificationEntity(
                            i.toLong(), resources.getInteger(R.integer.NOTIFICATION_TYPE_3),
                            "post",
                            "1",
                            "XXX에 댓글이 등록되었습니다.",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            System.currentTimeMillis() - i * 2000000,
                            r.nextInt(2)
                        )
                    )
                    arrayList.add(
                        NotificationEntity(
                            i.toLong(), resources.getInteger(R.integer.NOTIFICATION_TYPE_4),
                            "home",
                            "1",
                            "XXX가 당신을 팔로우합니다.",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            System.currentTimeMillis() - i * 3000000,
                            r.nextInt(2)
                        )
                    )
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF) -> {
                val userbrief = UserBrief(
                    "userID",
                    profileImgs[0].toString(),
                    "nickname",
                    ArrayList<String>(), 1
                )
                userbrief as T
            }
            resources.getInteger(R.integer.DATA_TYPE_USER_BRIEF_LIST) -> {
                val arrayList = ArrayList<UserBrief>()
                for (i in 0..9) {
                    val r = Random()
                    val follow = ArrayList<String>()
                    arrayList.add(
                        UserBrief(
                            "userID",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "nickname",
                            follow, 1
                        )
                    )
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATA_TYPE_USER_DETAIL) -> {
                val r = Random()
                val follow = ArrayList<String>()
                follow.add("유저1")
                val userDetail = UserDetail(
                    "userID",
                    profileImgs[r.nextInt(profileImgs.size)].toString(),
                    "유저",
                    "유저 소개글",
                    1,
                    follow,
                    1,
                    1
                )
                userDetail as T
            }
            resources.getInteger(R.integer.DATA_TYPE_POST) -> {
                val arrayList = ArrayList<Post>()
                for (i in 0..9) {
                    val r = Random()
                    val tags = ArrayList<String>()
                    tags.add("태그$i")
                    tags.add("태그$i")
                    val likes = ArrayList<String?>()
                    likes.add("유저$i")
                    val comments = ArrayList<Comment?>()
                    comments.add(
                        Comment(
                            i, i, "userID$i",
                            "유저 ", profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "내용 ", System.currentTimeMillis() - 1000 * 1000 * i
                        )
                    )
                    arrayList.add(
                        Post(
                            i,
                            "userID",
                            "유저 $i",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            postImgs[r.nextInt(postImgs.size)].toString(),
                            System.currentTimeMillis() - 1000 * 1000 * i,
                            "내용 $i",
                            tags,
                            comments,
                            likes
                        )
                    )
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATA_TYPE_COMMENTS) -> {
                val arrayList = ArrayList<Comment>()
                for (i in 0..9) {
                    val r = Random()
                    arrayList.add(
                        Comment(
                            i,
                            i,
                            "userID$i",
                            "유저 $i",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "내용 $i",
                            System.currentTimeMillis() - 1000 * 1000 * i
                        )
                    )
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATE_TYPE_RECIPE) -> {
                val arrayList = ArrayList<Recipe>()
                for (i in 0..19) {
                    val r = Random()
                    val ingredients = ArrayList<Ingredient>()
                    val ingredient = Ingredient("재료$i", i.toString() + "스푼")
                    ingredients.add(ingredient)
                    val tags = ArrayList<String>()
                    tags.add("태그 1")
                    tags.add("태그 2")
                    val recipe = Recipe(
                        i,
                        "레시피 $i",
                        "userID",
                        "유저 $i",
                        profileImgs[r.nextInt(profileImgs.size)].toString(),
                        recipeImgs[r.nextInt(recipeImgs.size)].toString(),
                        "내용 $i",
                        System.currentTimeMillis() - 1000 * 1000 * 20 * i,
                        i.toString() + "분",
                        i,
                        r.nextInt(51) * 0.1f,
                        ingredients,
                        tags
                    )
                    arrayList.add(recipe)
                }
                arrayList as T
            }
            resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL) -> {
                val ingredients = ArrayList<Ingredient>()
                ingredients.add(Ingredient("얇은 사각어묵", "6장"))
                ingredients.add(Ingredient("감자", "1개"))
                ingredients.add(Ingredient("물", "2/3컵"))
                ingredients.add(Ingredient("양파", "1/2개"))
                ingredients.add(Ingredient("봉어묵", "6개"))
                ingredients.add(Ingredient("대파", "1/2대"))
                ingredients.add(Ingredient("진간장", "5큰술"))
                ingredients.add(Ingredient("청양고추", "2개"))
                ingredients.add(Ingredient("간마늘", "1큰술"))
                ingredients.add(Ingredient("식용유", "2큰술"))
                ingredients.add(Ingredient("참기름", "2큰술"))
                ingredients.add(Ingredient("황설탕", "1큰술"))
                ingredients.add(Ingredient("고운고춧가루", "3/2큰술"))
                ingredients.add(Ingredient("통깨", "약간"))
                val tips = ArrayList<String>()
                tips.add("팁 1")
                tips.add("팁 2")
                tips.add("팁 2")
                tips.add("팁 2")
                tips.add("팁 2")
                tips.add("팁 2")
                tips.add("팁 2")
                val phases = ArrayList<RecipePhase>()
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_0.toString(),
                        "얇은 사각어묵은 길게 2등분 후 두께 2cm로 얇게 썰고, 봉어묵은 1cm정도 두께로 어슷 썰어 준비한다.",
                        ArrayList(listOf()),
                        "5분",
                        ArrayList(
                            listOf(
                                Ingredient("얇은 사각 어묵", "6장"),
                                Ingredient("봉어묵", "6개")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_1.toString(),
                        "양파는 가로로 반 갈라 2cm정도 두께로 자른다.",
                        ArrayList(
                            listOf(
                                "양파는 더 넣으셔도 돼요."
                            )
                        ),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("양파", "1/2개")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_2.toString(),
                        "대파, 청양고추는 0.3cm두께로 송송 썬다.",
                        ArrayList(
                            listOf(
                                "청양고추는 매우면 넣지 마세요."
                            )
                        ),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("대파", "1/2대"),
                                Ingredient("청양고추", "2개")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_3.toString(),
                        "감자는 십자모양으로 4등분 한 후 0.3cm 두께로 편 썬다.",
                        ArrayList(listOf()),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("감자", "1개")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_4.toString(),
                        "깊은 프라이팬에 어묵, 양파, 대파, 청양고추, 감자, 식용유, 간마늘을 넣고 강 불에 올린다.",
                        ArrayList(listOf()),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("손질한 재료들", "모두"),
                                Ingredient("간마늘", "1큰술"),
                                Ingredient("식용유", "2큰술")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_5.toString(),
                        "지글지글 소리가 나기 시작하면 전체적으로 섞일 때 까지 볶은 후 물, 진간장,  황설탕을 넣고 졸인다.",
                        ArrayList(
                            listOf(
                                "어묵의 종류나 불 세기에 따라 물양을 조절한다."
                            )
                        ),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("물", "2/3컵"),
                                Ingredient("진간장", "5큰술"),
                                Ingredient("황설탕", "1큰술")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_6.toString(),
                        " 어묵에 양념장이 배이면 고운고춧가루를 넣고 섞는다.",
                        ArrayList(listOf()),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("고운고춧가루", "3/2큰술")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_7.toString(),
                        "국물이 완전히 졸아들고 윤기가 나면 참기름을 넣고 섞는다.",
                        ArrayList(listOf()),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("참기름", "2큰술")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_8.toString(),
                        "통깨를 뿌린 후 접시에 옮겨 담아 완성한다.",
                        ArrayList(
                            listOf(
                                "그릇에 담고나서 통깨를 한번 더 뿌려줘도 좋다."
                            )
                        ),
                        "3분",
                        ArrayList(
                            listOf(
                                Ingredient("통깨", "약간")
                            )
                        )
                    )
                )
                val tags = ArrayList<String>()
                tags.add("백종원")
                tags.add("어묵")
                tags.add("반찬")
                val likes = ArrayList<String?>()
                likes.add("유저 1")
                likes.add("유저 2")
                val recipeDetail = RecipeDetail(
                    1, "백종원 어묵볶음", "userID", "Chef",
                    profileImgs[0].toString(),
                    R.raw.playrecipe_start.toString(),
                    """
                    반찬 걱정 덜어줄 어묵볶음!
                    바로 해서 먹으면 일품요리로도 손색없어요
                    """.trimIndent(),
                    System.currentTimeMillis() - 1000 * 1000,
                    "30분", 1, 5.0F, ingredients,
                    likes, tags, phases
                )
                recipeDetail as T
            }
            resources.getInteger(R.integer.DATA_TYPE_REVIEW) -> {
                val arrayList = ArrayList<Review>()
                for (i in 0..9) {
                    val r = Random()
                    arrayList.add(
                        Review(
                            i, i, "userID",
                            "유저 $i", profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "내용 $i", r.nextInt(51) * 0.1f,
                            System.currentTimeMillis() - 1000 * 1000 * i
                        )
                    )
                }
                arrayList as T
            }
            else -> {
                OtherUtil.log("no type")
                Any() as T
            }
        }
    }
}