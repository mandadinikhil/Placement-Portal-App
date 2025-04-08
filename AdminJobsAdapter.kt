package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.databinding.ItemAdminJobListBinding
import com.example.placementapp.models.Job
import java.text.SimpleDateFormat
import java.util.*

class AdminJobsAdapter(private val onJobClick: (Job) -> Unit) : 
    ListAdapter<Job, AdminJobsAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemAdminJobListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JobViewHolder(private val binding: ItemAdminJobListBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onJobClick(getItem(position))
                }
            }
        }

        fun bind(job: Job) {
            binding.apply {
                jobTitleText.text = job.title
                companyNameText.text = job.companyName
                applicationCountText.text = "${job.applicationCount ?: 0} Applications"
                
                // Format date
                job.timestamp?.let { timestamp ->
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    postedDateText.text = "Posted: ${sdf.format(timestamp.toDate())}"
                }
            }
        }
    }

    private class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
            return oldItem == newItem
        }
    }
} 