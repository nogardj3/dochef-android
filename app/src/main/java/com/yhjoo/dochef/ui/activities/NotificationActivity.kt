package com.yhjoo.dochef.ui.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.databinding.NotificationActivityBinding
import com.yhjoo.dochef.db.AppDatabase
import com.yhjoo.dochef.db.DataGenerator
import com.yhjoo.dochef.db.entity.NotificationEntity
import com.yhjoo.dochef.ui.adapter.NotificationListAdapter2
import com.yhjoo.dochef.viewmodel.NotificationViewModel
import kotlinx.coroutines.*

class NotificationActivity : BaseActivity() {
    private val binding: NotificationActivityBinding by lazy {
        NotificationActivityBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: NotificationViewModel by lazy {
        ViewModelProvider(this).get(
            NotificationViewModel::class.java
        )
    }

    private lateinit var notificationListAdapter: NotificationListAdapter2
    private var notifications: ArrayList<NotificationEntity> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.notificationToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel

        binding.apply {
            notificationListAdapter = NotificationListAdapter2(notifications).apply {
//            setEmptyView(
//                R.layout.rv_loading,
//                binding.notificationRecycler.parent as ViewGroup
//            )
//            setOnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
//                if (App.isServerAlive) {
//                    val db = NotificationDatabase.getInstance(applicationContext)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        db!!.notificationDao().setRead(notifications[position].id!!)
//                    }
//
//                    val notiType = notifications[position].type
//                    if (notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_1)
//                        || notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_2)
//                    ) {
//                        val intent =
//                            Intent(this@NotificationActivity, RecipeDetailActivity::class.java)
//                                .putExtra("recipeID", notifications[position].intentData.toInt())
//                        startActivity(intent)
//                    } else if (notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_3)) {
//                        val intent =
//                            Intent(this@NotificationActivity, PostDetailActivity::class.java)
//                                .putExtra("postID", notifications[position].intentData.toInt())
//                        startActivity(intent)
//                    } else if (notiType == resources.getInteger(R.integer.NOTIFICATION_TYPE_4)) {
//                        val intent = Intent(this@NotificationActivity, HomeActivity::class.java)
//                            .putExtra("postID", notifications[position].intentData)
//                        startActivity(intent)
//                    }
//                }
//            }
            }

            notificationRecycler.layoutManager = LinearLayoutManager(this@NotificationActivity)
            notificationRecycler.adapter = notificationListAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
//            notifications =
//                if (App.isServerAlive) {
//                    readDataFromDB()
//                } else {
//                    DataGenerator.make(
//                        resources,
//                        resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION)
//                    )
//                }
        }

//        notificationListAdapter.setNewData(notifications)
//        notificationListAdapter.setEmptyView(
//            R.layout.rv_empty_notification,
//            binding.notificationRecycler.parent as ViewGroup
//        )
    }

//    private suspend fun readDataFromDB(): ArrayList<NotificationEntity> =
//        withContext(Dispatchers.IO) {
//            val db = AppDatabase.getDatabase(applicationContext,viewModel)
//            val resList = ArrayList(
//                db!!.notificationDao()
//                    .getRecentList(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3)
//            )
//
//            resList
//        }
}