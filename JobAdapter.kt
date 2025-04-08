package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.databinding.ItemJobBinding
import com.example.placementapp.models.Job
import java.text.SimpleDateFormat
import java.util.Locale

class JobAdapter(
    private val onJobClick: (Job) -> Unit
) : ListAdapter<Job, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JobViewHolder(
        private val binding: ItemJobBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onJobClick(getItem(position))
                }
            }
        }

        fun bind(job: Job) {
            with(binding) {
                tvJobTitle.text = job.title
                tvCompanyName.text = job.companyName
                tvLocation.text = job.location
                tvSalary.text = job.salary
                tvLastDate.text = "Last Date: ${job.lastDate}"
                tvPostedDate.text = "Posted: ${formatDate(job.postedDate.toDate())}"
            }
        }

        private fun formatDate(date: java.util.Date): String {
            return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
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