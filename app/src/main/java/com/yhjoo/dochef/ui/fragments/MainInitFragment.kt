package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.model.Recipe
import com.yhjoo.dochef.databinding.FMainInitBinding
import com.yhjoo.dochef.ui.activities.RecipeDetailActivity
import com.yhjoo.dochef.ui.activities.RecipeThemeActivity
import com.yhjoo.dochef.ui.adapter.MainAdPagerAdapter
import com.yhjoo.dochef.ui.adapter.RecipeHorizontalAdapter
import com.yhjoo.dochef.utils.RetrofitBuilder
import com.yhjoo.dochef.utils.RetrofitServices.RecipeService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class MainInitFragment : Fragment() {
    /*
        TODO
        sampledb 만들어지면 TODO로 바꾸기
    */

    private lateinit var binding: FMainInitBinding
    private lateinit var recipeService: RecipeService
    private lateinit var recipeHorizontalAdapter: RecipeHorizontalAdapter
    private var recipeList = ArrayList<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FMainInitBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recipeService = RetrofitBuilder.create(requireContext(), RecipeService::class.java)

        val imgs = arrayListOf(R.raw.ad_temp_0, R.raw.ad_temp_1)

        binding.apply {
            mainAdviewpager.adapter = MainAdPagerAdapter(context, imgs)
            mainAdviewpagerIndicator.setViewPager(binding.mainAdviewpager)
            mainRecommendText.text = Html.fromHtml(
                String.format(getString(R.string.format_recommend_title), "Chef"),
                Html.FROM_HTML_MODE_LEGACY
            )
            mainRecommendMore.setOnClickListener {
//                    TODO
//                    Intent intent = new Intent(getContext(), RecipeThemeActivity.class)
//                            .putExtra("userID",recipeList.get(0).getUserID());
                startActivity(Intent(context, RecipeThemeActivity::class.java))
            }

            Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { count: Long ->
                    binding.mainAdviewpager.currentItem = count.toInt() % 2
                }

            recipeHorizontalAdapter = RecipeHorizontalAdapter().apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
                    val intent = Intent(
                        context, RecipeDetailActivity::class.java
                    )
                        .putExtra("recipeID", recipeList[position].recipeID)
                    startActivity(intent)
                }
                setNewData(recipeList)
            }
            mainRecommendRecyclerview.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mainRecommendRecyclerview.adapter = recipeHorizontalAdapter
        }

        if (App.isServerAlive)
            settingList()
        else {
            recipeList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            recipeHorizontalAdapter.setNewData(recipeList)
        }
        return view
    }

    private fun settingList() =         CoroutineScope(Dispatchers.Main).launch {
        runCatching {
            // Todo 유명인 레시피 검색
            // val res1 = recipeService.getRecipeByName( "CHEF","popular")
            val res1 = recipeService.getRecipes("popular")

            recipeList = res1.body()!!
            // Html.fromHtml(
            // String.format(getString(R.string.format_recommend_title),recipeList.get(0).getNickname()),Html.FROM_HTML_MODE_LEGACY);
            recipeHorizontalAdapter.setNewData(recipeList)
        }
            .onSuccess { }
            .onFailure {
                RetrofitBuilder.defaultErrorHandler(it)
            }
    }
}