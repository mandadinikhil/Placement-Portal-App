package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.R
import com.example.placementapp.models.JobApplication
import com.example.placementapp.models.Student
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AdminApplicationAdapter(
    private val onStatusChange: (JobApplication, String) -> Unit
) : ListAdapter<JobApplication, AdminApplicationAdapter.ViewHolder>(DiffCallback()) {
    
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val jobRole: TextView = itemView.findViewById(R.id.tvJobRole)
        val studentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val studentEmail: TextView = itemView.findViewById(R.id.tvStudentEmail)
        val appliedDate: TextView = itemView.findViewById(R.id.tvAppliedDate)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val acceptButton: Button = itemView.findViewById(R.id.btnAccept)
        val rejectButton: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val application = getItem(position)
        
        holder.companyName.text = application.companyName
        holder.jobRole.text = application.jobRole
        holder.appliedDate.text = "Applied on: ${dateFormat.format(application.appliedAt.toDate())}"
        holder.status.text = application.status.uppercase()

        // Fetch student details
        db.collection("students")
            .document(application.studentId)
            .get()
            .addOnSuccessListener { document ->
                val student = document.toObject(Student::class.java)
                holder.studentName.text = "Student: ${student?.fullName ?: "Unknown Student"}"
                holder.studentEmail.text = "Email: ${student?.collegeEmail ?: student?.personalEmail ?: "No email"}"
            }

        // Set background based on status
        val backgroundResId = when (application.status.lowercase()) {
            "pending" -> R.drawable.status_background_pending
            "accepted" -> R.drawable.status_background_accepted
            "rejected" -> R.drawable.status_background_rejected
            else -> R.drawable.status_background
        }
        holder.status.setBackgroundResource(backgroundResId)

        // Handle button visibility based on status
        val isPending = application.status.lowercase() == "pending"
        holder.acceptButton.visibility = if (isPending) View.VISIBLE else View.GONE
        holder.rejectButton.visibility = if (isPending) View.VISIBLE else View.GONE

        // Set button click listeners
        holder.acceptButton.setOnClickListener {
            onStatusChange(application, "accepted")
        }
        holder.rejectButton.setOnClickListener {
            onStatusChange(application, "rejected")
        }
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