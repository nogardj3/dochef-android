package com.yhjoo.dochef.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.NotificationItem
import com.yhjoo.dochef.databinding.ANotificationBinding
import com.yhjoo.dochef.ui.adapter.NotificationListAdapter
import com.yhjoo.dochef.utils.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class NotificationActivity : BaseActivity() {
    private val binding: ANotificationBinding by lazy { ANotificationBinding.inflate(layoutInflater) }

    private lateinit var chefSQLite: ChefSQLite
    private lateinit var notificationListAdapter: NotificationListAdapter
    private var notifications: ArrayList<NotificationItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.notificationToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        chefSQLite = ChefSQLite(
            this, ChefSQLite.DATABASE_NAME,
            null, ChefSQLite.DATABASE_VERSION
        )

        notificationListAdapter = NotificationListAdapter().apply {
            setEmptyView(
                R.layout.rv_loading,
                binding.notificationRecycler.parent as ViewGroup
            )
            setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
                if (App.isServerAlive) {
                    val db = NotificationDatabase.getInstance(applicationContext)
                    CoroutineScope(Dispatchers.IO).launch {
                        db!!.notificationDao().setLike(notifications[position].id!!)
                    }

                    val notiType = notifications[position].type
                    if (notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_1)
                        || notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_2)
                    ) {
                        val intent =
                            Intent(this@NotificationActivity, RecipeDetailActivity::class.java)
                                .putExtra("recipeID", notifications[position].intentData.toInt())
                        startActivity(intent)
                    } else if (notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_3)) {
                        val intent =
                            Intent(this@NotificationActivity, PostDetailActivity::class.java)
                                .putExtra("postID", notifications[position].intentData.toInt())
                        startActivity(intent)
                    } else if (notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_4)) {
                        val intent = Intent(this@NotificationActivity, HomeActivity::class.java)
                            .putExtra("postID", notifications[position].intentData)
                        startActivity(intent)
                    }
                }
            }
        }

        binding.notificationRecycler.layoutManager = LinearLayoutManager(this)
        binding.notificationRecycler.adapter = notificationListAdapter
        notificationListAdapter.setNewData(notifications)
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            notifications =
                if (App.isServerAlive) {
                    readDataFromDB()
                } else {
                    DataGenerator.make(
                        resources,
                        resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION)
                    )
                }
        }

        notificationListAdapter.setNewData(notifications)
        notificationListAdapter.setEmptyView(
            R.layout.rv_empty_notification,
            binding.notificationRecycler.parent as ViewGroup
        )
    }

    private suspend fun readDataFromDB(): ArrayList<NotificationItem> =
        withContext(Dispatchers.IO) {
            val db = NotificationDatabase.getInstance(applicationContext)
            val resList = ArrayList(
                db!!.notificationDao()
                    .getRecentList(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3)
            )

            resList
        }
}