package com.example.mthd.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mthd.R
import com.example.mthd.auth.LoginActivity
import com.example.mthd.admin.ResidentListActivity
import com.google.android.material.navigation.NavigationView
import database.DatabaseHelper

class AdminMainActivity : AppCompatActivity() {

    data class AdminFunction(val title: String, val iconRes: Int, val targetActivity: Class<*>? = null)

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dbHelper: DatabaseHelper
    private val functions = listOf(
        AdminFunction("Đăng ký cư dân mới", R.drawable.ic_register_user, RegisterResidentActivity::class.java),
        AdminFunction("Danh sách hợp đồng", R.drawable.ic_contract_list, ContractListActivity::class.java),
        AdminFunction("Danh sách cư dân", R.drawable.architect, ResidentListActivity::class.java),
        AdminFunction("Giấy tạm trú / tạm vắng", R.drawable.ic_temp_residence, AdminTempResidenceActivity::class.java),
        AdminFunction("Tạo thông báo", R.drawable.ic_notice,AdminCreateNotificationActivity::class.java)
    )
    private fun loadAdminName() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COL_FULLNAME),
            "${DatabaseHelper.COL_ROLE} = ?",
            arrayOf("admin"),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val adminName = cursor.getString(0)
            val headerView = navView.getHeaderView(0)
            val tvAdminName = headerView.findViewById<TextView>(R.id.tvAdminName)
            tvAdminName.text = adminName
        }
        cursor.close()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)
        // Khởi tạo DatabaseHelper
        dbHelper = DatabaseHelper(this)
        // Khởi tạo các view
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        // Gọi loadAdminName để hiển thị tên admin
        loadAdminName()
        setSupportActionBar(toolbar)
        supportActionBar?.title = "TRANG QUẢN TRỊ"

        // Toggle mở drawer
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Xử lý khi chọn item menu
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_info -> {
                    startActivity(Intent(this, AdminProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Gán GridLayout chức năng
        val gridLayout = findViewById<GridLayout>(R.id.gridAdminFunctions)
        val inflater = LayoutInflater.from(this)

        for (function in functions) {
            val itemView = inflater.inflate(R.layout.grid_item_function, gridLayout, false)

            val icon = itemView.findViewById<ImageView>(R.id.imgFunctionIcon)
            val title = itemView.findViewById<TextView>(R.id.tvFunctionTitle)

            icon.setImageResource(function.iconRes)
            title.text = function.title

            itemView.setOnClickListener {
                if (function.targetActivity != null) {
                    startActivity(Intent(this, function.targetActivity))
                } else {
                    Toast.makeText(this, "${function.title} đang được phát triển", Toast.LENGTH_SHORT).show()
                }
            }

            gridLayout.addView(itemView)
        }
    }

}
