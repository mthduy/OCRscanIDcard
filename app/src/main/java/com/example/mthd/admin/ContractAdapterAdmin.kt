package com.example.mthd.admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mthd.R
import com.example.mthd.admin.ContractDetailActivity
import model.Contract

class ContractAdapterAdmin(
    private val context: Context,
    private val contracts: List<Contract>
) : RecyclerView.Adapter<ContractAdapterAdmin.ContractViewHolder>() {

    class ContractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContractNumber: TextView = itemView.findViewById(R.id.tvContractNumber)
        val tvContractDate: TextView = itemView.findViewById(R.id.tvContractDate)
        val tvResidentName: TextView = itemView.findViewById(R.id.tvResName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_contract, parent, false)
        return ContractViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        val contract = contracts[position]
        holder.tvContractNumber.text = "Số hợp đồng: ${contract.soHopDong}"
        holder.tvContractDate.text = "Ngày ký: ${contract.ngayKy}"
        holder.tvResidentName.text = "Bên B: ${contract.residentName}"

        // Nhấn vào item mở ContractDetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ContractDetailActivity::class.java)
            intent.putExtra("contract_id", contract.contractId)
            intent.putExtra("role", "admin")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = contracts.size
}
