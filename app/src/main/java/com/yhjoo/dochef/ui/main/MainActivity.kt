package com.yhjoo.dochef.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.repository.PostRepository
import com.yhjoo.dochef.data.repository.RecipeRepository
import com.yhjoo.dochef.data.repository.UserRepository
import com.yhjoo.dochef.databinding.MainActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.notification.NotificationActivity
import com.yhjoo.dochef.ui.post.PostWriteActivity
import com.yhjoo.dochef.ui.recipe.RecipeMakeActivity
import com.yhjoo.dochef.ui.search.SearchActivity
import com.yhjoo.dochef.ui.setting.SettingActivity

class MainActivity : BaseActivity() {
    private val mainTabs = arrayOf(
        Pair(R.drawable.ic_home_white, InitFragment()),
        Pair(R.drawable.ic_hot_white, RecipesFragment()),
        Pair(R.drawable.ic_favorite_white, MyRecipeFragment()),
        Pair(R.drawable.ic_article_white, TimelineFragment()),
        Pair(R.drawable.ic_person_white, UserFragment())
    )

    private val binding: MainActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.main_activity)
    }
    private val mainViewModel: MainViewModel by viewModels{
        MainViewModelFactory(
            application,
            UserRepository(applicationContext),
            RecipeRepository(applicationContext),
            PostRepository(applicationContext)
        )
    }
    private lateinit var mainFragmentAdapter: FragmentStateAdapter

    private lateinit var powerMenu: PowerMenu
    lateinit var menuNotification: MenuItem
    lateinit var menuSearch: MenuItem
    lateinit var menuSort: MenuItem
    lateinit var menuWriteRecipe: MenuItem
    lateinit var menuWritePost: MenuItem
    lateinit var menuSetting: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
        }

        MobileAds.initialize(this)

        mainFragmentAdapter = MainFragmentAdapter(this)

        binding.apply {
            lifecycleOwner = this@MainActivity

            mainViewpager.apply {
                offscreenPageLimit = 5
                adapter = mainFragmentAdapter
                setPageTransformer(MarginPageTransformer(15))
            }

            TabLayoutMediator(mainTablayout, mainViewpager) { tab, position ->
                tab.setIcon(mainTabs[position].first)
            }.attach()
            mainTablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    mainViewpager.currentItem = tab.position
                    menuNotification.isVisible = false
                    menuSort.isVisible = false
                    menuWriteRecipe.isVisible = false
                    menuWritePost.isVisible = false
                    menuSearch.isVisible = false
                    menuSetting.isVisible = false
                    when (tab.position) {
                        0 -> menuNotification.isVisible = true
                        1 -> {
                            menuSort.isVisible = true
                            menuSearch.isVisible = true
                        }
                        2 -> {
                            menuWriteRecipe.isVisible = true
                            menuSearch.isVisible = true
                        }
                        3 -> {
                            menuWritePost.isVisible = true
                            menuSearch.isVisible = true
                        }
                        4 -> menuSetting.isVisible = true
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }

        powerMenu = PowerMenu.Builder(this)
            .addItem(PowerMenuItem("최신순", true))
            .addItem(PowerMenuItem("인기순", false))
            .addItem(PowerMenuItem("별점순", false))
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setMenuRadius(10f)
            .setMenuShadow(0f)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSelectedTextColor(Color.WHITE)
            .setTextGravity(Gravity.CENTER)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setBackgroundAlpha(0f)
            .setOnMenuItemClickListener { position, _ ->
                val mode = when (position) {
                    0 -> Constants.RECIPE.SORT.LATEST
                    1 -> Constants.RECIPE.SORT.POPULAR
                    2 -> Constants.RECIPE.SORT.RATING
                    else -> ""
                }
                mainViewModel.changeRecipesSort(mode)
                powerMenu.selectedPosition = position
                powerMenu.dismiss()
            }
            .build()
    }

    override fun onBackPressed() {
        MaterialDialog(this).show {
            message(text = "종료하시겠습니까?")
            positiveButton(text = "확인") {
                finish()
            }
            negativeButton(text = "취소")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menuNotification = menu.findItem(R.id.main_menu_notification)
        menuSort = menu.findItem(R.id.main_menu_sort)
        menuWriteRecipe = menu.findItem(R.id.main_menu_write_recipe)
        menuWritePost = menu.findItem(R.id.main_menu_write_post)
        menuSearch = menu.findItem(R.id.main_menu_search)
        menuSetting = menu.findItem(R.id.main_menu_setting)

        menuSort.isVisible = false
        menuWriteRecipe.isVisible = false
        menuWritePost.isVisible = false
        menuSearch.isVisible = false
        menuSetting.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_notification -> {
                startActivity(
                    Intent(
                        this,
                        NotificationActivity::class.java
                    )
                )
                true
            }
            R.id.main_menu_sort -> {
                powerMenu.showAsAnchorRightBottom(binding.mainToolbar)
                true
            }
            R.id.main_menu_write_recipe -> {
                startActivity(
                    Intent(this, RecipeMakeActivity::class.java)
                )
                true
            }
            R.id.main_menu_write_post -> {
                startActivity(
                    Intent(this, PostWriteActivity::class.java)
                        .putExtra("MODE", PostWriteActivity.Companion.UIMODE.WRITE)
                )
                true
            }
            R.id.main_menu_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            R.id.main_menu_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class MainFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return mainTabs.size
        }

        override fun createFragment(position: Int): Fragment {
            return mainTabs[position].second
        }
    }
}