package com.example.mthd.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mthd.R
import com.example.mthd.admin.ContractDetailActivity
import database.ContractDAO
import model.Contract


class MyContractsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var contractDAO: ContractDAO
    private var residentId: Int = -1
    private var contracts: List<Contract> = emptyList()
    private lateinit var cardContract: LinearLayout
    private lateinit var txtContractNumber: TextView
    private lateinit var txtTenantName: TextView
    private lateinit var txtContractDate: TextView
    private var currentContract: Contract? = null
    private lateinit var btnBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_contracts)

        cardContract = findViewById(R.id.cardContract)
        txtContractNumber = findViewById(R.id.txtContractNumber)
        txtTenantName = findViewById(R.id.txtTenantName)
        txtContractDate = findViewById(R.id.txtContractDate)
        btnBack = findViewById(R.id.btnBack)
        contractDAO = ContractDAO(this)

        residentId = intent.getIntExtra("residentId", -1)
        Log.d("MyContractsActivity", "residentId = $residentId")

        if (residentId == -1) {
            Toast.makeText(this, "Không tìm thấy cư dân!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

//        contracts = contractDAO.getContractsByResident(residentId)
//        Log.d("MyContractsActivity", "contracts size = ${contracts.size}")

        val contracts: List<Contract> = contractDAO.getContractsByResident(residentId)
        Log.d("MyContractsActivity", "contracts size = ${contracts.size}")

        if (contracts.isEmpty()) {
            Toast.makeText(this, "Bạn chưa có hợp đồng nào", Toast.LENGTH_SHORT).show()
        } else  {
            currentContract = contracts[0] // ✅ lấy hợp đồng đầu tiên
            txtContractNumber.text = "Số hợp đồng: ${currentContract?.soHopDong}"
            txtTenantName.text = "Người thuê: ${currentContract?.residentName ?: "Không rõ"}"
            txtContractDate.text = "Ngày ký: ${currentContract?.ngayKy}"
            cardContract.visibility = LinearLayout.VISIBLE
        }

        // ✅ mở chi tiết khi bấm card
        cardContract.setOnClickListener {
            currentContract?.let { contract ->
                val intent = Intent(this, ContractDetailActivity::class.java)
                intent.putExtra("contract_id", contract.contractId)
                intent.putExtra("role", "user")
                startActivity(intent)
            }
        }
    }
}
