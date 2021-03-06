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
                            "????????? XXX??? ??? ???????????? ?????????????????????.",
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
                            "XXX??? ??? ????????? ?????????????????????.",
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
                            "XXX??? ????????? ?????????????????????.",
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
                            "XXX??? ????????? ??????????????????.",
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
                follow.add("??????1")
                val userDetail = UserDetail(
                    "userID",
                    profileImgs[r.nextInt(profileImgs.size)].toString(),
                    "??????",
                    "?????? ?????????",
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
                    tags.add("??????$i")
                    tags.add("??????$i")
                    val likes = ArrayList<String?>()
                    likes.add("??????$i")
                    val comments = ArrayList<Comment?>()
                    comments.add(
                        Comment(
                            i, i, "userID$i",
                            "?????? ", profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "?????? ", System.currentTimeMillis() - 1000 * 1000 * i
                        )
                    )
                    arrayList.add(
                        Post(
                            i,
                            "userID",
                            "?????? $i",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            postImgs[r.nextInt(postImgs.size)].toString(),
                            System.currentTimeMillis() - 1000 * 1000 * i,
                            "?????? $i",
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
                            "?????? $i",
                            profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "?????? $i",
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
                    val ingredient = Ingredient("??????$i", i.toString() + "??????")
                    ingredients.add(ingredient)
                    val tags = ArrayList<String>()
                    tags.add("?????? 1")
                    tags.add("?????? 2")
                    val recipe = Recipe(
                        i,
                        "????????? $i",
                        "userID",
                        "?????? $i",
                        profileImgs[r.nextInt(profileImgs.size)].toString(),
                        recipeImgs[r.nextInt(recipeImgs.size)].toString(),
                        "?????? $i",
                        System.currentTimeMillis() - 1000 * 1000 * 20 * i,
                        i.toString() + "???",
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
                ingredients.add(Ingredient("?????? ????????????", "6???"))
                ingredients.add(Ingredient("??????", "1???"))
                ingredients.add(Ingredient("???", "2/3???"))
                ingredients.add(Ingredient("??????", "1/2???"))
                ingredients.add(Ingredient("?????????", "6???"))
                ingredients.add(Ingredient("??????", "1/2???"))
                ingredients.add(Ingredient("?????????", "5??????"))
                ingredients.add(Ingredient("????????????", "2???"))
                ingredients.add(Ingredient("?????????", "1??????"))
                ingredients.add(Ingredient("?????????", "2??????"))
                ingredients.add(Ingredient("?????????", "2??????"))
                ingredients.add(Ingredient("?????????", "1??????"))
                ingredients.add(Ingredient("??????????????????", "3/2??????"))
                ingredients.add(Ingredient("??????", "??????"))
                val tips = ArrayList<String>()
                tips.add("??? 1")
                tips.add("??? 2")
                tips.add("??? 2")
                tips.add("??? 2")
                tips.add("??? 2")
                tips.add("??? 2")
                tips.add("??? 2")
                val phases = ArrayList<RecipePhase>()
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_0.toString(),
                        "?????? ??????????????? ?????? 2?????? ??? ?????? 2cm??? ?????? ??????, ???????????? 1cm?????? ????????? ?????? ?????? ????????????.",
                        ArrayList(listOf()),
                        "5???",
                        ArrayList(
                            listOf(
                                Ingredient("?????? ?????? ??????", "6???"),
                                Ingredient("?????????", "6???")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_1.toString(),
                        "????????? ????????? ??? ?????? 2cm?????? ????????? ?????????.",
                        ArrayList(
                            listOf(
                                "????????? ??? ???????????? ??????."
                            )
                        ),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("??????", "1/2???")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_2.toString(),
                        "??????, ??????????????? 0.3cm????????? ?????? ??????.",
                        ArrayList(
                            listOf(
                                "??????????????? ????????? ?????? ?????????."
                            )
                        ),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("??????", "1/2???"),
                                Ingredient("????????????", "2???")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_3.toString(),
                        "????????? ?????????????????? 4?????? ??? ??? 0.3cm ????????? ??? ??????.",
                        ArrayList(listOf()),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("??????", "1???")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_4.toString(),
                        "?????? ??????????????? ??????, ??????, ??????, ????????????, ??????, ?????????, ???????????? ?????? ??? ?????? ?????????.",
                        ArrayList(listOf()),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("????????? ?????????", "??????"),
                                Ingredient("?????????", "1??????"),
                                Ingredient("?????????", "2??????")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_5.toString(),
                        "???????????? ????????? ?????? ???????????? ??????????????? ?????? ??? ?????? ?????? ??? ???, ?????????,  ???????????? ?????? ?????????.",
                        ArrayList(
                            listOf(
                                "????????? ????????? ??? ????????? ?????? ????????? ????????????."
                            )
                        ),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("???", "2/3???"),
                                Ingredient("?????????", "5??????"),
                                Ingredient("?????????", "1??????")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_6.toString(),
                        " ????????? ???????????? ????????? ????????????????????? ?????? ?????????.",
                        ArrayList(listOf()),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("??????????????????", "3/2??????")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_7.toString(),
                        "????????? ????????? ???????????? ????????? ?????? ???????????? ?????? ?????????.",
                        ArrayList(listOf()),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("?????????", "2??????")
                            )
                        )
                    )
                )
                phases.add(
                    RecipePhase(
                        R.raw.playrecipe_8.toString(),
                        "????????? ?????? ??? ????????? ?????? ?????? ????????????.",
                        ArrayList(
                            listOf(
                                "????????? ???????????? ????????? ?????? ??? ???????????? ??????."
                            )
                        ),
                        "3???",
                        ArrayList(
                            listOf(
                                Ingredient("??????", "??????")
                            )
                        )
                    )
                )
                val tags = ArrayList<String>()
                tags.add("?????????")
                tags.add("??????")
                tags.add("??????")
                val likes = ArrayList<String?>()
                likes.add("?????? 1")
                likes.add("?????? 2")
                val recipeDetail = RecipeDetail(
                    1, "????????? ????????????", "userID", "Chef",
                    profileImgs[0].toString(),
                    R.raw.playrecipe_start.toString(),
                    """
                    ?????? ?????? ????????? ????????????!
                    ?????? ?????? ????????? ?????????????????? ???????????????
                    """.trimIndent(),
                    System.currentTimeMillis() - 1000 * 1000,
                    "30???", 1, 5.0F, ingredients,
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
                            "?????? $i", profileImgs[r.nextInt(profileImgs.size)].toString(),
                            "?????? $i", r.nextInt(51) * 0.1f,
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