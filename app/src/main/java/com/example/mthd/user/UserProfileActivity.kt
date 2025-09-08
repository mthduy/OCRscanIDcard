package com.example.mthd.user

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import database.ResidentDAO
import model.Resident

class UserProfileActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var btnUpdate: Button
    private lateinit var tvFullName: EditText
    private lateinit var tvBirthDate: EditText
    private lateinit var tvSex: EditText
    private lateinit var tvIdNumber: EditText
    private lateinit var tvOrigin: EditText
    private lateinit var tvResidence: EditText
    private lateinit var tvExpiry: EditText

    private lateinit var residentDAO: ResidentDAO
    private var currentResident: Resident? = null
    private var residentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Mapping view
        btnBack = findViewById(R.id.btnBack)
        btnUpdate = findViewById(R.id.btnUpdate)
        tvFullName = findViewById(R.id.tvFullName)
        tvBirthDate = findViewById(R.id.tvBirthDate)
        tvSex = findViewById(R.id.tvSex)
        tvIdNumber = findViewById(R.id.tvIdNumber)
        tvOrigin = findViewById(R.id.tvOrigin)
        tvResidence = findViewById(R.id.tvResidence)
        tvExpiry = findViewById(R.id.tvExpiry)

        residentDAO = ResidentDAO(this)

        // Lấy residentId từ intent
        residentId = intent.getIntExtra("residentId", -1)
        if (residentId != -1) {
            loadResident(residentId)
        }

        // Nút quay lại
        btnBack.setOnClickListener {
            finish()
        }

        // Nút cập nhật
        btnUpdate.setOnClickListener {
            updateResident()
        }
    }

    private fun loadResident(residentId: Int) {
        val resident: Resident? = residentDAO.getResidentById(residentId)
        resident?.let {
            currentResident = it
            tvFullName.setText(it.fullName)
            tvBirthDate.setText(it.birthDate)
            tvSex.setText(it.sex)
            tvIdNumber.setText(it.idNumber)
            tvOrigin.setText(it.origin)
            tvResidence.setText(it.residence)
            tvExpiry.setText(it.expiry)
        }
    }

    private fun updateResident() {
        if (currentResident == null) return

        val updatedResident = Resident(
            residentId = residentId,
            fullName = tvFullName.text.toString(),
            birthDate = tvBirthDate.text.toString(),
            sex = tvSex.text.toString(),
            idNumber = tvIdNumber.text.toString(),
            origin = tvOrigin.text.toString(),
            residence = tvResidence.text.toString(),
            expiry = tvExpiry.text.toString()
        )

        val result = residentDAO.updateResident(updatedResident)
        if (result > 0) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            currentResident = updatedResident
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
        }
    }
}
