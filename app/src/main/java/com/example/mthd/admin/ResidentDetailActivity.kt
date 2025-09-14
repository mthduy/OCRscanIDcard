package com.example.mthd.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R

class ResidentDetailActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var etSex: EditText
    private lateinit var etIdNumber: EditText
    private lateinit var etOrigin: EditText
    private lateinit var etResidence: EditText
    private lateinit var etExpiry: EditText
    private lateinit var btnUpdate: Button

    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_detail)

        // Ánh xạ view
        etFullName = findViewById(R.id.tvFullName)
        etBirthDate = findViewById(R.id.tvBirthDate)
        etSex = findViewById(R.id.tvSex)
        etIdNumber = findViewById(R.id.tvIdNumber)
        etOrigin = findViewById(R.id.tvOrigin)
        etResidence = findViewById(R.id.tvResidence)
        etExpiry = findViewById(R.id.tvExpiry)
        btnUpdate = findViewById(R.id.btnUpdate)

        // Nhận dữ liệu từ Intent
        val residentId = intent.getIntExtra("residentId", 0)
        val residentName = intent.getStringExtra("residentName") ?: ""
        val residentDob = intent.getStringExtra("residentDob") ?: ""
        val residentSex = intent.getStringExtra("residentSex") ?: ""
        val residentCCCD = intent.getStringExtra("residentCCCD") ?: ""
        val residentOrigin = intent.getStringExtra("residentOrigin") ?: ""
        val residentAddress = intent.getStringExtra("residentAddress") ?: ""
        val residentExpiry = intent.getStringExtra("residentExpiry") ?: ""
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // quay về activity trước
        }
        // Hiển thị dữ liệu lên EditText
        etFullName.setText(residentName)
        etBirthDate.setText(residentDob)
        etSex.setText(residentSex)
        etIdNumber.setText(residentCCCD)
        etOrigin.setText(residentOrigin)
        etResidence.setText(residentAddress)
        etExpiry.setText(residentExpiry)

        // Xử lý cập nhật (sau này nối DB)
        btnUpdate.setOnClickListener {
            // Lấy dữ liệu mới từ EditText
            val updatedName = etFullName.text.toString()
            val updatedDob = etBirthDate.text.toString()
            val updatedSex = etSex.text.toString()
            val updatedCCCD = etIdNumber.text.toString()
            val updatedOrigin = etOrigin.text.toString()
            val updatedResidence = etResidence.text.toString()
            val updatedExpiry = etExpiry.text.toString()

            // TODO: Update vào database qua ResidentDAO
            // residentDAO.updateResident(...)

            // Hiện Toast báo thành công (demo)
            android.widget.Toast.makeText(
                this,
                "Cập nhật cư dân $updatedName thành công!",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            finish() // quay lại danh sách
        }
    }
}
