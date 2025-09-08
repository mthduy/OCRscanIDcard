package com.example.mthd.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import com.example.mthd.auth.LoginActivity
import database.AdminDAO

class AdminProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)

        val tvName = findViewById<TextView>(R.id.tvAdminName)
        val tvEmail = findViewById<TextView>(R.id.tvAdminEmail)

        val imgBack = findViewById<ImageButton>(R.id.btnBack)
        val imgLogout = findViewById<ImageButton>(R.id.btnLogout)


        // Lấy admin từ database
        val adminId = 1 // ví dụ hoặc lấy từ SharedPreferences
        val adminDAO = AdminDAO(this)
        val admin = adminDAO.getAdminById(adminId)
        if (admin != null) {
            tvName.text = "Tên Admin: ${admin.fullName}"
            tvEmail.text = "Email: ${admin.gmail}"
        }

        // Xử lý quay lại
        imgBack.setOnClickListener {
            finish() // trở về màn hình trước
        }

        // Xử lý đăng xuất
        imgLogout.setOnClickListener {
            // Xóa session / SharedPreferences nếu có
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
