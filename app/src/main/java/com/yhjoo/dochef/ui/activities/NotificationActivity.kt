package com.yhjoo.dochef.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yhjoo.dochef.App
import com.yhjoo.dochef.R
import com.yhjoo.dochef.adapter.NotificationListAdapter
import com.yhjoo.dochef.data.DataGenerator
import com.yhjoo.dochef.data.model.Notification
import com.yhjoo.dochef.databinding.ANotificationBinding
import com.yhjoo.dochef.model.*
import com.yhjoo.dochef.ui.activities.BaseActivity
import com.yhjoo.dochef.ui.activities.HomeActivity
import com.yhjoo.dochef.utils.*
import com.yhjoo.dochef.utils.ChefSQLite.NotificationEntry
import java.util.*

class NotificationActivity : BaseActivity() {
    var binding: ANotificationBinding? = null
    var chefSQLite: ChefSQLite? = null
    var notificationListAdapter: NotificationListAdapter? = null
    var notifications = ArrayList<Notification>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ANotificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.notificationToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        chefSQLite = ChefSQLite(
            this, ChefSQLite.Companion.DATABASE_NAME,
            null, ChefSQLite.Companion.DATABASE_VERSION
        )
        notificationListAdapter = NotificationListAdapter()
        notificationListAdapter!!.setEmptyView(
            R.layout.rv_loading,
            binding!!.notificationRecycler.parent as ViewGroup
        )
        notificationListAdapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            if (App.isServerAlive()) {
                val db2 = chefSQLite!!.writableDatabase
                val values = ContentValues()
                values.put(NotificationEntry.COLUMN_NAME_READ, 1)
                val selection = BaseColumns._ID + " LIKE ?"
                val selectionArgs = arrayOf(Integer.toString(notifications[position]._id))
                val count = db2.update(
                    NotificationEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
                )
                val noti_type = notifications[position].type
                Utils.log(notifications[position].type)
                if (noti_type == resources.getInteger(R.integer.NOTIFICATION_TYPE_1)
                    || noti_type == resources.getInteger(R.integer.NOTIFICATION_TYPE_2)
                ) {
                    val intent = Intent(this@NotificationActivity, RecipeDetailActivity::class.java)
                        .putExtra("recipeID", notifications[position].intentData.toInt())
                    startActivity(intent)
                } else if (noti_type == resources.getInteger(R.integer.NOTIFICATION_TYPE_3)) {
                    val intent = Intent(this@NotificationActivity, PostDetailActivity::class.java)
                        .putExtra("postID", notifications[position].intentData.toInt())
                    startActivity(intent)
                } else if (noti_type == resources.getInteger(R.integer.NOTIFICATION_TYPE_4)) {
                    val intent = Intent(this@NotificationActivity, HomeActivity::class.java)
                        .putExtra("postID", notifications[position].intentData)
                    startActivity(intent)
                }
            }
        }
        binding!!.notificationRecycler.layoutManager = LinearLayoutManager(this)
        binding!!.notificationRecycler.adapter = notificationListAdapter
        notificationListAdapter!!.setNewData(notifications)
    }

    override fun onResume() {
        super.onResume()
        notifications = if (App.isServerAlive()) {
            readDataFromDB()
        } else {
            DataGenerator.make(
                resources,
                resources.getInteger(R.integer.DATA_TYPE_NOTIFICATION)
            )
        }
        notificationListAdapter!!.setNewData(notifications)
        notificationListAdapter!!.setEmptyView(
            R.layout.rv_empty_notification,
            binding!!.notificationRecycler.parent as ViewGroup
        )
    }

    fun readDataFromDB(): ArrayList<Notification> {
        val res = ArrayList<Notification>()
        val db = chefSQLite!!.readableDatabase
        val selection = NotificationEntry.COLUMN_NAME_DATETIME + " > ?"
        val selectionArgs =
            arrayOf(java.lang.Long.toString(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3))
        val sortOrder = NotificationEntry.COLUMN_NAME_DATETIME + " DESC"
        val cursor = db.query(
            NotificationEntry.TABLE_NAME,  // The table to query
            null,  // The array of columns to return (pass null to get all)
            selection,  // The columns for the WHERE clause
            selectionArgs,  // The values for the WHERE clause
            null,  // don't group the rows
            null,  // don't filter by row groups
            sortOrder // The sort order
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