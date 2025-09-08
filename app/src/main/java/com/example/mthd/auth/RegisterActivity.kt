package com.example.mthd.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import database.UserDAO
import model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtFullName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtBirthDate: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var edtGmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edtFullName = findViewById(R.id.edtFullName)
        edtPhone = findViewById(R.id.edtPhone)
        edtBirthDate = findViewById(R.id.edtBirthDate)
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        edtGmail = findViewById(R.id.edtGmail) // ánh xạ gmail
        btnRegister = findViewById(R.id.btnRegister)

        val userDAO = UserDAO(this)

        btnRegister.setOnClickListener {
            val fullName = edtFullName.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val birthDate = edtBirthDate.text.toString().trim()
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val gmail = edtGmail.text.toString().trim()

            // Kiểm tra input
            if (fullName.isEmpty() || phone.isEmpty() || birthDate.isEmpty() ||
                username.isEmpty() || password.isEmpty() || gmail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra username đã tồn tại
            if (userDAO.isUsernameExists(username)) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo user mới với role = "user"
            val newUser = User(
                fullName = fullName,
                phone = phone,
                birthDate = birthDate,
                username = username,
                password = password,
                role = "user",
                gmail = gmail
            )

            val success = userDAO.registerUser(newUser)

            if (success) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }

}
