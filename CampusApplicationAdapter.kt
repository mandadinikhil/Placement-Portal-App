package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.R
import com.example.placementapp.models.CampusRecruitmentApplication
import java.text.SimpleDateFormat
import java.util.*

class CampusApplicationAdapter(
    private val applications: List<CampusRecruitmentApplication>
) : RecyclerView.Adapter<CampusApplicationAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val jobRole: TextView = itemView.findViewById(R.id.tvJobRole)
        val appliedDate: TextView = itemView.findViewById(R.id.tvAppliedDate)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campus_application, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = applications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = applications[position]
        
        holder.companyName.text = application.companyName
        holder.jobRole.text = application.jobRole
        holder.appliedDate.text = "Applied on: ${dateFormat.format(application.appliedAt.toDate())}"
        holder.status.text = application.status.uppercase()

        // Set background based on status
        val backgroundResId = when (application.status.lowercase()) {
            "pending" -> R.drawable.status_background_pending
            "accepted" -> R.drawable.status_background_accepted
            "rejected" -> R.drawable.status_background_rejected
            else -> R.drawable.status_background
        }
        holder.status.setBackgroundResource(backgroundResId)
    }
} 