package com.example.mthd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.auth.LoginActivity
import database.UserDAO

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userDAO = UserDAO(this)
        userDAO.keepDatabaseOpen() // Gọi hàm này càng sớm càng tốt

        // Mở thẳng LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
