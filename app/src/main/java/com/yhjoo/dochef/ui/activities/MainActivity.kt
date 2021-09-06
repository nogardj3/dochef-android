package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.R
import com.yhjoo.dochef.activities.*
import com.yhjoo.dochef.ui.adapter.MainFragmentAdapter
import com.yhjoo.dochef.databinding.AMainBinding
import com.yhjoo.dochef.ui.fragments.*
import com.yhjoo.dochef.utils.Utils
import java.util.*

class MainActivity : BaseActivity() {
    private val tabIcons = intArrayOf(
        R.drawable.ic_home_white, R.drawable.ic_hot_white,
        R.drawable.ic_favorite_white, R.drawable.ic_article_white, R.drawable.ic_person_white
    )

    val binding: AMainBinding by lazy { AMainBinding.inflate(layoutInflater) }
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mainFragmentAdapter: MainFragmentAdapter

    private lateinit var powerMenu: PowerMenu
    lateinit var menuNotification: MenuItem
    lateinit var menuSearch: MenuItem
    lateinit var menuSort: MenuItem
    lateinit var menuWriteRecipe: MenuItem
    lateinit var menuWritePost: MenuItem
    lateinit var menuSetting: MenuItem
    lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowTitleEnabled(false)

        MobileAds.initialize(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        userID = Utils.getUserBrief(this).userID
        val fragments = ArrayList<Fragment>().apply {
            add(MainInitFragment())
            add(MainRecipesFragment())
            add(MainMyRecipeFragment())
            add(MainTimelineFragment())
            add(MainUserFragment())
        }
        mainFragmentAdapter = MainFragmentAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            fragments
        )

        binding.apply {
            mainViewpager.offscreenPageLimit = 3
            mainViewpager.pageMargin = 15
            mainViewpager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    mainTablayout
                )
            )
            mainViewpager.adapter = mainFragmentAdapter
            for (drawable in tabIcons) {
                mainTablayout.addTab(mainTablayout.newTab().setIcon(drawable))
            }
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
            .build()
        powerMenu.onMenuItemClickListener =
            OnMenuItemClickListener { position: Int, _: PowerMenuItem? ->
                when (position) {
                    0 -> sortMenu(MainRecipesFragment.VALUES.SORT.LATEST)
                    1 -> sortMenu(MainRecipesFragment.VALUES.SORT.POPULAR)
                    2 -> sortMenu(MainRecipesFragment.VALUES.SORT.RATING)
                }
                powerMenu.selectedPosition = position
                powerMenu.dismiss()
            }
    }

    override fun onBackPressed() {
        MaterialDialog(this).show {
            message(text = "종료하시겠습니까?")
            positiveButton(text = "확인") {
                val bundle = Bundle()
                bundle.apply {
                    putString(
                        FirebaseAnalytics.Param.ITEM_ID,
                        getString(R.string.analytics_id_terminated)
                    )
                    putString(
                        FirebaseAnalytics.Param.ITEM_NAME,
                        getString(R.string.analytics_name_terminated)
                    )
                    putString(
                        FirebaseAnalytics.Param.CONTENT_TYPE,
                        getString(R.string.analytics_type_text)
                    )
                }

                mFirebaseAnalytics.logEvent(
                    getString(R.string.analytics_event_terminated),
                    bundle
                )
                finish()
            }
            negativeButton(text = "취소")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

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
        when (item.itemId) {
            R.id.main_menu_notification -> startActivity(
                Intent(
                    this,
                    NotificationActivity::class.java
                )
            )
            R.id.main_menu_sort -> powerMenu.showAsAnchorRightBottom(binding.mainToolbar)
            R.id.main_menu_write_recipe -> startActivity(
                Intent(
                    this,
                    RecipeMakeActivity::class.java
                )
            )
            R.id.main_menu_write_post -> {
                val intent = Intent(this, PostWriteActivity::class.java)
                    .putExtra("MODE", PostWriteActivity.VALUES.UIMODE.WRITE)
                startActivity(intent)
            }
            R.id.main_menu_search -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.main_menu_setting -> startActivity(Intent(this, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortMenu(sort: Int) {
        (mainFragmentAdapter.getItem(1) as MainRecipesFragment).changeSortMode(sort)
    }
}