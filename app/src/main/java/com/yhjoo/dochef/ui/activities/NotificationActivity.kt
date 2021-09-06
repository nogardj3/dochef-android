package com.yhjoo.dochef.ui.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.ui.adapter.NotificationListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Notification
import com.yhjoo.dochef.databinding.ANotificationBinding
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.ChefSQLite.NotificationEntry
import java.util.*

class NotificationActivity : BaseActivity() {
    private val binding: ANotificationBinding by lazy { ANotificationBinding.inflate(layoutInflater) }

    private lateinit var chefSQLite: ChefSQLite
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var notifications: ArrayList<Notification>

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
                    val db2 = chefSQLite.writableDatabase
                    val values = ContentValues().apply {
                        put(NotificationEntry.COLUMN_NAME_READ, 1)
                    }

                    val selection = BaseColumns._ID + " LIKE ?"
                    val selectionArgs = arrayOf(notifications[position].type.toString())
                    db2.update(
                        NotificationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                    )

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
        notifications = if (App.isServerAlive) {
            readDataFromDB()
        } else {
            DataGenerator.make(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION)
            )
        }
        notificationListAdapter.setNewData(notifications)
        notificationListAdapter.setEmptyView(
            R.layout.rv_empty_notification,
            binding.notificationRecycler.parent as ViewGroup
        )
    }

    private fun readDataFromDB(): ArrayList<Notification> {
        val res = ArrayList<Notification>()
        val db = chefSQLite.readableDatabase
        val selection = NotificationEntry.COLUMN_NAME_DATETIME + " > ?"
        val selectionArgs =
            arrayOf((System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3).toString())
        val sortOrder = NotificationEntry.COLUMN_NAME_DATETIME + " DESC"
        val cursor = db.query(
            NotificationEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
        while (cursor.moveToNext()) {
            res.add(
                Notification(
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow(BaseColumns._ID)
                    ),
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_TYPE)
                    ),
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_INTENT)
                    ),
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_INTENT_DATA)
                    ),
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_CONTENTS)
                    ),
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_IMG)
                    ),
                    cursor.getLong(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_DATETIME)
                    ),
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow(NotificationEntry.COLUMN_NAME_READ)
                    )
                )
            )
        }
        cursor.close()
        for (noti in res) {
            Utils.log(noti.toString())
        }
        return res
    }
}