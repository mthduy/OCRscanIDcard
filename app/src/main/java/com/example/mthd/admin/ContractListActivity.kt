package com.example.mthd.admin

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.DatabaseHelper
import com.example.mthd.R

import model.Contract

class ContractListActivity : AppCompatActivity() {

    private lateinit var rvContracts: RecyclerView

    private lateinit var btnback: Button
    private lateinit var dbHelper: DatabaseHelper
    private val contractList = mutableListOf<Contract>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_list)

        rvContracts = findViewById(R.id.rvContracts)
        rvContracts.layoutManager = LinearLayoutManager(this)
        // Nút back
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // quay về activity trước
        }
        dbHelper = DatabaseHelper(this)
        loadContracts()
    }

    private fun loadContracts() {
        contractList.clear()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.rawQuery(
            "SELECT c.${DatabaseHelper.COL_CONTRACT_ID}, " +
                    "c.${DatabaseHelper.COL_SO_HOP_DONG}, " +
                    "c.${DatabaseHelper.COL_NGAY_KY}, " +
                    "c.${DatabaseHelper.COL_BEN_A}, " + // lấy thêm cột benA
                    "c.${DatabaseHelper.COL_BEN_B_ID}, " +
                    "r.${DatabaseHelper.COL_HO_TEN} " +
                    "FROM ${DatabaseHelper.TABLE_CONTRACTS} c " +
                    "JOIN ${DatabaseHelper.TABLE_RESIDENTS} r " +
                    "ON c.${DatabaseHelper.COL_BEN_B_ID} = r.${DatabaseHelper.COL_RESIDENT_ID}",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val contract = Contract(
                    contractId = cursor.getLong(0),
                    soHopDong = cursor.getString(1),
                    ngayKy = cursor.getString(2),
                    benA = cursor.getString(3),
                    benBId = cursor.getInt(4),
                    residentName = cursor.getString(5)
                )
                contractList.add(contract)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        rvContracts.adapter = ContractAdapterAdmin(this, contractList)
    }


}
