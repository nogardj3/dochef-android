package com.yhjoo.dochef.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.common.adapter.RecipeHorizontalAdapter
import com.yhjoo.dochef.databinding.MainInitFragmentBinding
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeThemeActivity
import com.yhjoo.dochef.data.network.RetrofitBuilder
import com.yhjoo.dochef.data.network.RetrofitServices.RecipeService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class MainInitFragment : Fragment() {
    /* TODO
    1. Theme activity 이동 설정
    2.
    */

    private lateinit var binding: MainInitFragmentBinding
    private lateinit var recipeService: RecipeService
    private lateinit var recipeHorizontalAdapter: RecipeHorizontalAdapter
    private var recipeList = ArrayList<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainInitFragmentBinding.inflate(inflater, container, false)
        val view: View = binding.root

        recipeService = RetrofitBuilder.create(requireContext(), RecipeService::class.java)

        val imgs = arrayOf(R.raw.ad_temp_0, R.raw.ad_temp_1)

        binding.apply {
            mainInitAdviewpager.adapter = MainAdPagerAdapter(requireContext(), imgs)
            mainInitAdviewpagerIndicator.setViewPager2(mainInitAdviewpager)
            Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { count: Long ->
                    binding.mainInitAdviewpager.currentItem = count.toInt() % 2
                }

            mainInitRecommendText.text = Html.fromHtml(
                String.format(getString(R.string.format_recommend_title), "Chef"),
                Html.FROM_HTML_MODE_LEGACY
            )
            mainInitRecommendMore.setOnClickListener {
                val itt = Intent(requireContext(), RecipeThemeActivity::class.java)
                    .putExtra("userID", recipeList[0].userID)
                startActivity(Intent(context, RecipeThemeActivity::class.java))
            }

            recipeHorizontalAdapter = RecipeHorizontalAdapter().apply {
                setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
                    val intent = Intent(
                        requireContext(), RecipeDetailActivity::class.java
                    )
                        .putExtra("recipeID", recipeList[position].recipeID)
                    startActivity(intent)
                }
                setNewData(recipeList)
            }
            mainInitRecommendRecyclerview.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = recipeHorizontalAdapter
            }
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

    private fun settingList() = CoroutineScope(Dispatchers.Main).launch {
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

//    class MainAdPagerAdapter(var mContext: Context?, private var imgids: ArrayList<Int>) : PagerAdapter() {
//        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
//            val aa = AppCompatImageView(mContext!!)
//            val lp = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            aa.layoutParams = lp
//            collection.addView(aa)
//            return aa
//        }
//
//        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
//            collection.removeView(view as View)
//        }
//
//        override fun getCount(): Int {
//            return imgids.size
//        }
//
//        override fun isViewFromObject(view: View, `object`: Any): Boolean {
//            return view === `object`
//        }
//    }

    class MainAdPagerAdapter(private val context: Context, private val img_ids: Array<Int>) :
        RecyclerView.Adapter<MainAdPagerAdapter.ADViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                ADViewHolder = ADViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_init_adview, parent, false)
        )

        override fun onBindViewHolder(holder: ADViewHolder, position: Int) {
            holder.adItem.setImageResource(img_ids[position])
        }

        override fun getItemCount(): Int = img_ids.size

        class ADViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val adItem: AppCompatImageView = view.findViewById(R.id.adview_item)
        }
    }
}