package com.yhjoo.dochef.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.MainFragmentAdapter
import com.yhjoo.dochef.databinding.AMainBinding
import com.yhjoo.dochef.fragments.*
import com.yhjoo.dochef.ui.fragments.*
import com.yhjoo.dochef.ui.fragments.MainRecipesFragment.SORT
import com.yhjoo.dochef.utils.*
import java.util.*

class MainActivity : BaseActivity() {
    var tabIcons = intArrayOf(
        R.drawable.ic_home_white, R.drawable.ic_hot_white,
        R.drawable.ic_favorite_white, R.drawable.ic_article_white, R.drawable.ic_person_white
    )
    var binding: AMainBinding? = null
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var mainFragmentAdapter: MainFragmentAdapter? = null
    var powerMenu: PowerMenu? = null
    var menu_notification: MenuItem? = null
    var menu_search: MenuItem? = null
    var menu_sort: MenuItem? = null
    var menu_write_recipe: MenuItem? = null
    var menu_write_post: MenuItem? = null
    var menu_setting: MenuItem? = null
    var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.mainToolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowTitleEnabled(false)
        MobileAds.initialize(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        if (Utils.getUserBrief(this) == null) {
            ChefAuth.logOut(this)
            finish()
        }
        userID = Utils.getUserBrief(this).userID
        val fragments = ArrayList<Fragment>()
        fragments.add(MainInitFragment())
        fragments.add(MainRecipesFragment())
        fragments.add(MainMyRecipeFragment())
        fragments.add(MainTimelineFragment())
        fragments.add(MainUserFragment())
        mainFragmentAdapter = MainFragmentAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            fragments
        )
        binding!!.mainViewpager.offscreenPageLimit = 3
        binding!!.mainViewpager.pageMargin = 15
        binding!!.mainViewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding!!.mainTablayout
            )
        )
        binding!!.mainViewpager.adapter = mainFragmentAdapter
        for (drawable in tabIcons) {
            binding!!.mainTablayout.addTab(binding!!.mainTablayout.newTab().setIcon(drawable))
        }
        binding!!.mainTablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding!!.mainViewpager.currentItem = tab.position
                menu_notification!!.isVisible = false
                menu_sort!!.isVisible = false
                menu_write_recipe!!.isVisible = false
                menu_write_post!!.isVisible = false
                menu_search!!.isVisible = false
                menu_setting!!.isVisible = false
                when (tab.position) {
                    0 -> menu_notification!!.isVisible = true
                    1 -> {
                        menu_sort!!.isVisible = true
                        menu_search!!.isVisible = true
                    }
                    2 -> {
                        menu_write_recipe!!.isVisible = true
                        menu_search!!.isVisible = true
                    }
                    3 -> {
                        menu_write_post!!.isVisible = true
                        menu_search!!.isVisible = true
                    }
                    4 -> menu_setting!!.isVisible = true
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
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
        powerMenu.setOnMenuItemClickListener(OnMenuItemClickListener { position: Int, item: PowerMenuItem? ->
            if (position == 0) sortMenu(SORT.LATEST) else if (position == 1) sortMenu(SORT.POPULAR) else if (position == 2) sortMenu(
                SORT.RATING
            )
            powerMenu.setSelectedPosition(position)
            powerMenu.dismiss()
        })
    }

    override fun onRestart() {
        println("####################################")
        super.onRestart()
    }

    override fun onBackPressed() {
        BaseActivity.Companion.createConfirmDialog(this,
            null, "종료하시겠습니까?",
            SingleButtonCallback { dialog: MaterialDialog?, which: DialogAction? ->
                val bundle = Bundle()
                bundle.putString(
                    FirebaseAnalytics.Param.ITEM_ID,
                    getString(R.string.analytics_id_terminated)
                )
                bundle.putString(
                    FirebaseAnalytics.Param.ITEM_NAME,
                    getString(R.string.analytics_name_terminated)
                )
                bundle.putString(
                    FirebaseAnalytics.Param.CONTENT_TYPE,
                    getString(R.string.analytics_type_text)
                )
                mFirebaseAnalytics!!.logEvent(
                    getString(R.string.analytics_event_terminated),
                    bundle
                )
                finish()
            }).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu_notification = menu.findItem(R.id.main_menu_notification)
        menu_sort = menu.findItem(R.id.main_menu_sort)
        menu_write_recipe = menu.findItem(R.id.main_menu_write_recipe)
        menu_write_post = menu.findItem(R.id.main_menu_write_post)
        menu_search = menu.findItem(R.id.main_menu_search)
        menu_setting = menu.findItem(R.id.main_menu_setting)
        menu_sort.setVisible(false)
        menu_write_recipe.setVisible(false)
        menu_write_post.setVisible(false)
        menu_search.setVisible(false)
        menu_setting.setVisible(false)
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
            R.id.main_menu_sort -> powerMenu!!.showAsAnchorRightBottom(binding!!.mainToolbar)
            R.id.main_menu_write_recipe -> startActivity(
                Intent(
                    this,
                    RecipeMakeActivity::class.java
                )
            )
            R.id.main_menu_write_post -> {
                val intent = Intent(this, PostWriteActivity::class.java)
                    .putExtra("MODE", PostWriteActivity.MODE.WRITE)
                startActivity(intent)
            }
            R.id.main_menu_search -> startActivity(Intent(this, SearchActivity::class.java))
            R.id.main_menu_setting -> startActivity(Intent(this, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    fun sortMenu(sort: SORT) {
        (mainFragmentAdapter!!.getItem(1) as MainRecipesFragment).changeSortMode(sort)
    }
}