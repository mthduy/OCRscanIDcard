package com.example.mthd.user

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mthd.R
import database.DatabaseHelper
import model.Notification

class UserNotificationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val notifications = ArrayList<Notification>()
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_notifications)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.rvNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(notifications)
        recyclerView.adapter = adapter
        val btnExit: Button = findViewById(R.id.btnExit)
        btnExit.setOnClickListener {
            finish() // thoát Activity
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        notifications.clear()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_NOTIFICATIONS,
            arrayOf("id", DatabaseHelper.COL_TITLE, DatabaseHelper.COL_MESSAGE, DatabaseHelper.COL_CREATED_AT),
            null, null, null, null,
            "${DatabaseHelper.COL_CREATED_AT} DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val notification = Notification(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)),
                    message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MESSAGE)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CREATED_AT)),
                    author = "Admin" // có thể thêm cột author trong DB nếu cần
                )
                notifications.add(notification)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        adapter.notifyDataSetChanged()
    }
}
