package com.example.mthd.admin

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class AdminCreateNotificationActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etTitle: EditText
    private lateinit var etMessage: EditText
    private lateinit var btnSave: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_notification)

        dbHelper = DatabaseHelper(this)

        etTitle = findViewById(R.id.etTitle)
        etMessage = findViewById(R.id.etMessage)
        btnSave = findViewById(R.id.btnSaveNotification)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_TITLE, title)
                put(DatabaseHelper.COL_MESSAGE, message)
                put(DatabaseHelper.COL_CREATED_AT, getCurrentTime())
            }

            val result = db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, values)
            if (result != -1L) {
                Toast.makeText(this, "Tạo thông báo thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi lưu thông báo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
