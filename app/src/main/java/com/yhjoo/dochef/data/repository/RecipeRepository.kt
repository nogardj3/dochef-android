package com.yhjoo.dochef.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.RECIPE
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.model.RecipeDetail
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.*

class RecipeRepository(
    private val context: Context
) {
    private val recipeClient =
        RetrofitBuilder.create(context, RetrofitServices.RecipeService::class.java)

    @WorkerThread
    suspend fun getRecipeDetail(recipeId: Int): Flow<Response<RecipeDetail>> {
        return flow {
            if (App.isServerAlive)
                emit(recipeClient.getRecipeDetail(recipeId))
            else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATA_TYPE_RECIPE_DETAIL)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun getRecipeList(
        searchby: Int,
        sort: String,
        searchValue: String?
    ): Flow<Response<ArrayList<Recipe>>> {
        return flow {
            if (App.isServerAlive) {
                when (searchby) {
                    RECIPE.SEARCHBY.USERID -> emit(
                        recipeClient.getRecipeByUserID(
                            searchValue!!,
                            sort
                        )
                    )
                    RECIPE.SEARCHBY.INGREDIENT -> emit(
                        recipeClient.getRecipeByIngredient(
                            searchValue!!,
                            sort
                        )
                    )
                    RECIPE.SEARCHBY.RECIPENAME -> emit(
                        recipeClient.getRecipeByName(
                            searchValue!!,
                            sort
                        )
                    )
                    RECIPE.SEARCHBY.TAG -> emit(recipeClient.getRecipeByTag(searchValue!!, sort))
                    else -> emit(recipeClient.getRecipes(sort))
                }
            } else
                emit(
                    Response.success(
                        DataGenerator.make(
                            context.resources,
                            context.resources.getInteger(R.integer.DATE_TYPE_RECIPE)
                        )
                    )
                )
        }
    }

    @WorkerThread
    suspend fun likeRecipe(recipeId: Int, userId: String): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(recipeClient.setLikeRecipe(recipeId, userId, 1))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun dislikeRecipe(recipeId: Int, userId: String): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(recipeClient.setLikeRecipe(recipeId, userId, -1))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun deleteRecipe(recipeId: Int, userId: String): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(recipeClient.deleteRecipe(recipeId, userId))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }

    @WorkerThread
    suspend fun addCount(recipeId: Int): Flow<Response<JsonObject>> {
        return flow {
            if (App.isServerAlive)
                emit(recipeClient.addCount(recipeId))
            else {
                val jsonObject = JsonObject()
                emit(Response.success(jsonObject))
            }
        }
    }
}