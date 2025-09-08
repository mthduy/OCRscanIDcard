package com.example.mthd.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.mthd.R
import com.google.android.material.navigation.NavigationView

class UserMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var gridUserFunctions: GridLayout
    private lateinit var navView: NavigationView
    private var residentId: Int = -1  // 👉 biến lưu residentId
    private var role: String = "user"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        residentId = intent.getIntExtra("residentId", -1)
        drawerLayout = findViewById(R.id.drawer_layout_user)
        gridUserFunctions = findViewById(R.id.gridUserFunctions)
        navView = findViewById(R.id.nav_view_user)

        // Setup Toolbar + Drawer Toggle
        val toolbar: Toolbar = findViewById(R.id.toolbar_user)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)



        // Thêm các chức năng vào GridLayout
        addFunctionItem("Hồ sơ cá nhân", R.drawable.ic_person) {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("residentId", residentId)   // Truyền residentId
            startActivity(intent)
        }

        addFunctionItem("Hợp đồng của tôi", R.drawable.ic_contract) {
            val intent = Intent(this, MyContractsActivity::class.java)
            intent.putExtra("residentId", residentId)  // 👈 truyền residentId
            intent.putExtra("role", role)
            startActivity(intent)
        }
        addFunctionItem("Đăng ký tạm trú", R.drawable.ic_scan) {
            val intent = Intent(this, RegisterTempResidenceActivity::class.java)
            intent.putExtra("residentId", residentId)
            startActivity(intent)
        }

        addFunctionItem("Xem thông báo", R.drawable.ic_notification) {
            val intent = Intent(this, UserNotificationsActivity::class.java)
            intent.putExtra("residentId", residentId)
            startActivity(intent)
        }


//        addFunctionItem("Đăng xuất", R.drawable.ic_logout) {
//            finish() // Hoặc quay về màn hình đăng nhập
//        }
    }

    private fun addFunctionItem(title: String, iconRes: Int, onClick: () -> Unit) {
        val itemView = LayoutInflater.from(this)
            .inflate(R.layout.item_user_function, gridUserFunctions, false) as LinearLayout

        itemView.findViewById<android.widget.ImageView>(R.id.imgFunctionIcon).setImageResource(iconRes)
        itemView.findViewById<TextView>(R.id.tvFunctionTitle).text = title

        itemView.setOnClickListener { onClick() }

        gridUserFunctions.addView(itemView)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("residentId", residentId) // 👉 truyền residentId qua
                startActivity(intent)
            }

            R.id.nav_logout -> {
                // Quay về LoginActivity và xóa backstack
                val intent = Intent(this, com.example.mthd.auth.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
