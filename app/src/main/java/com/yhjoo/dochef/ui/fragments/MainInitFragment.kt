package com.yhjoo.dochef.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.activities.RecipeDetailActivity
import com.yhjoo.dochef.activities.RecipeThemeActivity
import com.yhjoo.dochef.adapter.MainAdPagerAdapter
import com.yhjoo.dochef.adapter.RecipeHorizontalAdapter
import com.yhjoo.dochef.databinding.FMainInitBinding
import com.yhjoo.dochef.utils.RxRetrofitServices.RecipeService
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.utils.RxRetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class MainInitFragment : Fragment() {
    var binding: FMainInitBinding? = null
    var recipeService: RecipeService? = null
    var recipeHorizontalAdapter: RecipeHorizontalAdapter? = null
    var recipeList: ArrayList<Recipe?>? = null

    /*
        TODO
        sampledb 만들어지면 TODO로 바꾸기
    */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FMainInitBinding.inflate(inflater, container, false)
        val view: View = binding!!.root
        recipeService = RxRetrofitBuilder.create(
            this.context,
            RecipeService::class.java
        )
        val imgs = ArrayList<Int>()
        imgs.add(R.raw.ad_temp_0)
        imgs.add(R.raw.ad_temp_1)
        binding!!.mainAdviewpager.adapter = MainAdPagerAdapter(context, imgs)
        binding!!.mainAdviewpagerIndicator.setViewPager(binding!!.mainAdviewpager)
        binding!!.mainRecommendText.text = Html.fromHtml(
            String.format(getString(R.string.format_recommend_title), "Chef"),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding!!.mainRecommendMore.setOnClickListener { v: View? ->
            //TODO
//                    Intent intent = new Intent(getContext(), RecipeThemeActivity.class)
//                            .putExtra("userID",recipeList.get(0).getUserID());
            startActivity(Intent(context, RecipeThemeActivity::class.java))
        }
        Observable.interval(5, TimeUnit.SECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { count: Long -> binding!!.mainAdviewpager.currentItem = count.toInt() % 2 }
        recipeHorizontalAdapter = RecipeHorizontalAdapter()
        recipeHorizontalAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view1: View?, position: Int ->
            val intent = Intent(
                context, RecipeDetailActivity::class.java
            )
                .putExtra("recipeID", recipeList!![position].getRecipeID())
            startActivity(intent)
        }
        recipeHorizontalAdapter!!.setNewData(recipeList)
        binding!!.mainRecommendRecyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding!!.mainRecommendRecyclerview.adapter = recipeHorizontalAdapter
        if (App.isServerAlive()) recipelist else {
            recipeList = DataGenerator.make(
                resources, resources.getInteger(R.integer.DATE_TYPE_RECIPE)
            )
            recipeHorizontalAdapter!!.setNewData(recipeList)
        }
        return view
    }// Html.fromHtml(

    // String.format(getString(R.string.format_recommend_title),recipeList.get(0).getNickname()),Html.FROM_HTML_MODE_LEGACY);
    // Todo
    // recipeService.getRecipeByName( "CHEF","popular")
    val recipelist: Unit
        get() {
            (activity as BaseActivity?).getCompositeDisposable().add( // Todo
                // recipeService.getRecipeByName( "CHEF","popular")
                recipeService!!.getRecipes("popular")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response: Response<ArrayList<Recipe?>?>? ->
                        recipeList = response!!.body()
                        // Html.fromHtml(
                        // String.format(getString(R.string.format_recommend_title),recipeList.get(0).getNickname()),Html.FROM_HTML_MODE_LEGACY);
                        recipeHorizontalAdapter!!.setNewData(recipeList)
                    }, RxRetrofitBuilder.defaultConsumer())
            )
        }
}