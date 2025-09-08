package com.example.mthd.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import com.example.mthd.admin.AdminMainActivity
import com.example.mthd.user.UserMainActivity
import database.UserDAO

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        val userDAO = UserDAO(this)

        // Đăng nhập
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userDAO.loginUser(username, password)
            if (user != null) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                // Điều hướng theo role
                if (user.role == "admin") {
                    val intent = Intent(this, AdminMainActivity::class.java)
                    intent.putExtra("role", "admin")
                    startActivity(intent)
                } else {
                    val intent = Intent(this, UserMainActivity::class.java)
                    intent.putExtra("role", "user")
                    intent.putExtra("residentId", user.residentId ?: -1) // 👈 TRUYỀN residentId
                    startActivity(intent)                }
                finish()

            } else {
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }

        }

        // Chuyển sang RegisterActivity
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
