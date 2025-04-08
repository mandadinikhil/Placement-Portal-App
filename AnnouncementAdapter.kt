package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.R
import com.example.placementapp.models.Announcement
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnnouncementAdapter(
    private val announcements: List<Announcement>,
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()),
    private val onEditClick: ((Announcement) -> Unit)? = null,
    private val onDeleteClick: ((Announcement) -> Unit)? = null,
    private val isAdminView: Boolean = false
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvCreatedBy: TextView = view.findViewById(R.id.tvCreatedBy)
        val btnEdit: MaterialButton = view.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        
        holder.tvTitle.text = announcement.title
        holder.tvContent.text = announcement.content
        holder.tvDate.text = dateFormat.format(Date(announcement.timestamp))
        holder.tvCreatedBy.text = "Posted by: ${announcement.createdByName}"

        // Show/hide edit and delete buttons based on admin view
        if (isAdminView) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
            
            holder.btnEdit.setOnClickListener { onEditClick?.invoke(announcement) }
            holder.btnDelete.setOnClickListener { onDeleteClick?.invoke(announcement) }
        } else {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount() = announcements.size
} 