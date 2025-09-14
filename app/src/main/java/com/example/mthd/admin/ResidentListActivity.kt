package com.example.mthd.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import adapter.ResidentAdapter
import android.widget.Button
import android.widget.ImageButton
import database.ResidentDAO
import model.Resident
import com.example.mthd.R
import com.example.mthd.admin.ResidentDetailActivity

class ResidentListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var residentDAO: ResidentDAO
    private lateinit var btnBack: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_list)

        recyclerView = findViewById(R.id.recyclerViewResidents)
        residentDAO = ResidentDAO(this)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // quay về activity trước
        }
        val residents = residentDAO.getAllResidents()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ResidentAdapter(residents) { resident ->
            val intent = Intent(this, ResidentDetailActivity::class.java)
            intent.putExtra("residentId", resident.residentId)
            intent.putExtra("residentName", resident.fullName)
            intent.putExtra("residentCCCD", resident.idNumber)
            intent.putExtra("residentDob", resident.birthDate)
            intent.putExtra("residentAddress", resident.residence)
            startActivity(intent)
        }

    }
}
