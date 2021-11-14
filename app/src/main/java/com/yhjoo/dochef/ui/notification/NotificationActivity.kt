package com.yhjoo.dochef.ui.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.yhjoo.dochef.App
import com.yhjoo.dochef.Constants
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.NotificationActivityBinding
import com.yhjoo.dochef.ui.base.BaseActivity
import com.yhjoo.dochef.ui.home.HomeActivity
import com.yhjoo.dochef.ui.post.PostDetailActivity
import com.yhjoo.dochef.ui.recipe.RecipeDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {
    private val binding: NotificationActivityBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.notification_activity)
    }
    private val notificationViewModel: NotificationViewModel by viewModels()

    private lateinit var notificationListAdapter: NotificationListAdapter

    private var _eventResult = MutableSharedFlow<Pair<Any, String?>>()
    val eventResult = _eventResult.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.notificationToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            lifecycleOwner = this@NotificationActivity

            notificationListAdapter = NotificationListAdapter(notificationViewModel)
            notificationRecycler.adapter = notificationListAdapter
        }

        notificationViewModel.allnotifications
            .observe(this@NotificationActivity, {
                binding.notificationEmpty.isVisible = it.isEmpty()
                notificationListAdapter.submitList(it) {
                    binding.notificationRecycler.scrollToPosition(0)
                }
            })

        subscribeEventOnLifecycle {
            notificationViewModel.eventResult.collect {
                if (it.first == NotificationViewModel.Events.ISCLICKED) {
                    if (App.isServerAlive) {
                        val intent = when (it.second.type) {
                            resources.getInteger(R.integer.NOTIFICATION_TYPE_1),
                            resources.getInteger(R.integer.NOTIFICATION_TYPE_2) -> {
                                Intent(this@NotificationActivity, RecipeDetailActivity::class.java)
                                    .putExtra(Constants.INTENTNAME.RECIPE_ID, it.second.intentData.toInt())
                            }
                            resources.getInteger(R.integer.NOTIFICATION_TYPE_3) -> {
                                Intent(this@NotificationActivity, PostDetailActivity::class.java)
                                    .putExtra(Constants.INTENTNAME.POST_ID, it.second.intentData.toInt())
                            }
                            else -> {
                                Intent(this@NotificationActivity, HomeActivity::class.java)
                                    .putExtra(Constants.INTENTNAME.USER_ID, it.second.intentData)
                            }
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}