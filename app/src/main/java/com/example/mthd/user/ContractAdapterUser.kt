package com.example.mthd.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mthd.R

import model.Contract


class ContractAdapterUser(
    private val context: Context,
    private val list: List<Contract>
) : BaseAdapter() {

    override fun getCount(): Int = list.size
    override fun getItem(position: Int): Any = list[position]
    override fun getItemId(position: Int): Long = list[position].contractId.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_contract, parent, false)

        val tvContractNumber = view.findViewById<TextView>(R.id.tvContractNumber)
        val tvContractDate = view.findViewById<TextView>(R.id.tvContractDate)
        val tvResName = view.findViewById<TextView>(R.id.tvResName)

        val contract = list[position]
        tvContractNumber.text = "Số HĐ: ${contract.soHopDong}"
        tvContractDate.text = "Ngày ký: ${contract.ngayKy}"
        tvResName.text = "Bên B: ${contract.residentName}"

        return view
    }
}
