package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.model.Recipe
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainInitFragmentBinding
import com.yhjoo.dochef.ui.base.BaseFragment
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeRecommendActivity
import com.yhjoo.dochef.utils.OtherUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class InitFragment : BaseFragment() {
    // TODO
    // RecyclerView listitem Databinding
    // Recommend Tag

    private val imgs = arrayOf(R.raw.ad_temp_0, R.raw.ad_temp_1)

    private lateinit var binding: MainInitFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            UserRepository(requireContext().applicationContext),
            RecipeRepository(requireContext().applicationContext),
            PostRepository(requireContext().applicationContext)
        )
    }

    private lateinit var initRecipeListAdapter: InitRecipeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_init_fragment, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@InitFragment

            mainInitAdviewpager.adapter = MainAdPagerAdapter(imgs)
            mainInitAdviewpagerIndicator.setViewPager2(mainInitAdviewpager)

            initRecipeListAdapter = InitRecipeListAdapter(this@InitFragment)
            mainInitRecommendRecycler.adapter = initRecipeListAdapter
        }

        Observable.interval(5, TimeUnit.SECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { count: Long ->
                binding.mainInitAdviewpager.currentItem = count.toInt() % imgs.size
            }

        mainViewModel.allRecommendList.observe(viewLifecycleOwner, {
            binding.recipesEmpty.isVisible = it.isEmpty()
            initRecipeListAdapter.submitList(it) {}
        })

        return binding.root
    }

    fun goRecommend() {
        startActivity(
            Intent(requireContext(), RecipeRecommendActivity::class.java)
                .putExtra("tag", "백종원")
        )
    }

    fun goRecipeDetail(item: Recipe) {
        startActivity(
            Intent(requireContext(), RecipeDetailActivity::class.java)
                .putExtra("recipeID", item.recipeID)
        )
    }

    class MainAdPagerAdapter(private val img_ids: Array<Int>) :
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