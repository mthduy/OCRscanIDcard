package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import model.Resident
import com.example.mthd.R

class ResidentAdapter(
    private val residents: List<Resident>,
    private val onItemClick: (Resident) -> Unit
) : RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder>() {

    inner class ResidentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvResidentName)
        val tvCCCD: TextView = view.findViewById(R.id.tvResidentCCCD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resident, parent, false)
        return ResidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        val resident = residents[position]
        holder.tvName.text = resident.fullName
        holder.tvCCCD.text = "CCCD: ${resident.idNumber}"

        holder.itemView.setOnClickListener {
            onItemClick(resident)
        }
    }

    override fun getItemCount() = residents.size
}
