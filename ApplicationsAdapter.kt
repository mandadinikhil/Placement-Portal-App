package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.databinding.ItemApplicationBinding
import com.example.placementapp.models.Application
import java.text.SimpleDateFormat
import java.util.*

class ApplicationsAdapter : ListAdapter<Application, ApplicationsAdapter.ApplicationViewHolder>(ApplicationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val binding = ItemApplicationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ApplicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ApplicationViewHolder(private val binding: ItemApplicationBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(application: Application) {
            binding.apply {
                nameTextView.text = application.name
                emailTextView.text = application.email
                phoneTextView.text = application.phone
                statusTextView.text = "Status: ${application.status.capitalize()}"
                
                application.timestamp?.let { timestamp ->
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    dateTextView.text = "Applied: ${sdf.format(timestamp.toDate())}"
                }
            }
        }
    }

    private class ApplicationDiffCallback : DiffUtil.ItemCallback<Application>() {
        override fun areItemsTheSame(oldItem: Application, newItem: Application): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Application, newItem: Application): Boolean {
            return oldItem == newItem
        }
    }
} 