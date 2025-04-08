package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.R
import com.example.placementapp.models.JobApplication
import java.text.SimpleDateFormat
import java.util.*

class JobApplicationAdapter : ListAdapter<JobApplication, JobApplicationAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val jobRole: TextView = itemView.findViewById(R.id.tvJobRole)
        val appliedDate: TextView = itemView.findViewById(R.id.tvAppliedDate)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = getItem(position)
        
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

    class DiffCallback : DiffUtil.ItemCallback<JobApplication>() {
        override fun areItemsTheSame(oldItem: JobApplication, newItem: JobApplication): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JobApplication, newItem: JobApplication): Boolean {
            return oldItem == newItem
        }
    }
} 