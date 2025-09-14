package com.example.mthd.user

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mthd.R
import database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class RegisterTempResidenceActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etDob: EditText
    private lateinit var etSex: EditText
    private lateinit var etIdNumber: EditText
    private lateinit var etOrigin: EditText
    private lateinit var etResidence: EditText
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etReason: EditText
    private lateinit var btnSave: Button
    private lateinit var toolbar: Toolbar
    private lateinit var tvStatus: TextView

    private lateinit var dbHelper: DatabaseHelper
    private var residentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_temp_residence)


        // Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Ánh xạ view
        tvStatus = findViewById(R.id.tvStatus)
        etFullName = findViewById(R.id.etFullName)
        etDob = findViewById(R.id.etDob)
        etSex = findViewById(R.id.etSex)
        etIdNumber = findViewById(R.id.etIdNumber)
        etOrigin = findViewById(R.id.etOrigin)
        etResidence = findViewById(R.id.etResidence)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etReason = findViewById(R.id.etReason)
        btnSave = findViewById(R.id.btnSave)

        dbHelper = DatabaseHelper(this)

        // Lấy residentId từ intent
        residentId = intent.getIntExtra("residentId", -1)
        if (residentId != -1) {
            loadResidentInfo(residentId)
        }

        // Ngày bắt đầu = ngày hiện tại
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        etStartDate.setText(today)
        loadResidentInfo(residentId)
        checkExistingRequest(residentId)
        btnSave.setOnClickListener {
            saveTempResidence()
        }
    }

    private fun loadResidentInfo(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RESIDENTS,
            null,
            "${DatabaseHelper.COL_RESIDENT_ID}=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                etFullName.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_HO_TEN)))
                etDob.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_SINH)))
                etSex.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_GIOI_TINH)))
                etIdNumber.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_CCCD)))
                etOrigin.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_QUE_QUAN)))
                etResidence.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_NOI_THUONG_TRU)))
            }
        }
    }

    private fun saveTempResidence() {
        val endDate = etEndDate.text.toString().trim()
        val reason = etReason.text.toString().trim()
        val startDate = etStartDate.text.toString().trim()

        if (endDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ngày kết thúc và lý do", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_RESIDENT_ID, residentId) // dùng đúng cột đã định nghĩa
            put(DatabaseHelper.COL_START_DATE, startDate)
            put(DatabaseHelper.COL_END_DATE, endDate)
            put(DatabaseHelper.COL_REASON, reason)
        }


        val newId = db.insert(DatabaseHelper.TABLE_TEMP_RESIDENCE, null, values)
        if (newId == -1L) {
            Toast.makeText(this, "Lưu đăng ký thất bại", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Đăng ký tạm trú thành công", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun checkExistingRequest(residentId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TEMP_RESIDENCE,
            null,
            "${DatabaseHelper.COL_RESIDENT_ID}=?",
            arrayOf(residentId.toString()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                // Điền thông tin
                etStartDate.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_START_DATE)))
                etEndDate.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_END_DATE)))
                etReason.setText(it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_REASON)))

                // Vô hiệu nút lưu
                btnSave.isEnabled = false
                btnSave.text = "Đã gửi"

                val status = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS))
                if (status == "received") {
                    tvStatus.visibility = TextView.VISIBLE
                    tvStatus.text = "Đã tiếp nhận"
                }
            }
        }
    }
}