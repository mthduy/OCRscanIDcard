package com.example.mthd.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import database.TemporaryResidenceDAO

class AdminTempResidenceActivity : AppCompatActivity() {

    private lateinit var dao: TemporaryResidenceDAO
    private lateinit var container: LinearLayout

    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_temp_residence)

        dao = TemporaryResidenceDAO(this)
        container = findViewById(R.id.containerTempResidence)
        // Nút trở về
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Kết thúc activity hiện tại, quay về màn trước
        }
        loadAllRequests()
    }

    private fun loadAllRequests() {
        container.removeAllViews()
        val requests = dao.getAllRequests()
        val inflater = LayoutInflater.from(this)

        for (request in requests) {
            val view = inflater.inflate(R.layout.item_temp_residence, container, false)
            val tvInfo = view.findViewById<TextView>(R.id.tvTempInfo)
            val btnReceive = view.findViewById<Button>(R.id.btnReceive)

            tvInfo.text = "ResidentID: ${request.residentId}\nFrom: ${request.startDate} To: ${request.endDate}\nReason: ${request.reason}\nStatus: ${request.status}"

            if (request.status == "received") {
                btnReceive.isEnabled = false
                btnReceive.text = "Đã tiếp nhận"
            } else {
                btnReceive.setOnClickListener {
                    dao.updateStatus(request.id, "received")
                    Toast.makeText(this, "Đã tiếp nhận!", Toast.LENGTH_SHORT).show()
                    loadAllRequests()
                }
            }

            container.addView(view)
        }
    }
}
