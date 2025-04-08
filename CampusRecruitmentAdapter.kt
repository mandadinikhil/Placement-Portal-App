package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.placementapp.databinding.ItemCampusRecruitmentBinding
import com.example.placementapp.models.CampusRecruitment
import java.text.SimpleDateFormat
import java.util.*

class CampusRecruitmentAdapter(
    private val isAdmin: Boolean,
    private val onItemClick: (CampusRecruitment) -> Unit,
    private val onApplyClick: (CampusRecruitment) -> Unit
) : ListAdapter<CampusRecruitment, CampusRecruitmentAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCampusRecruitmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, isAdmin, onItemClick, onApplyClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemCampusRecruitmentBinding,
        private val isAdmin: Boolean,
        private val onItemClick: (CampusRecruitment) -> Unit,
        private val onApplyClick: (CampusRecruitment) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun bind(recruitment: CampusRecruitment) {
            with(binding) {
                tvCompanyName.text = recruitment.companyName
                tvJobRole.text = recruitment.jobRole
                tvDescription.text = recruitment.description
                tvEligibility.text = "Eligibility: ${recruitment.eligibilityCriteria}"
                tvPackage.text = "Package: ${recruitment.package_}"
                tvDriveDate.text = "Drive Date: ${dateFormat.format(recruitment.driveDate.toDate())}"
                tvLastDate.text = "Last Date to Apply: ${dateFormat.format(recruitment.lastDateToApply.toDate())}"

                // Show Apply button only for students (non-admin) view
                btnApply.visibility = if (!isAdmin) ViewGroup.VISIBLE else ViewGroup.GONE

                root.setOnClickListener {
                    onItemClick(recruitment)
                }

                btnApply.setOnClickListener {
                    onApplyClick(recruitment)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CampusRecruitment>() {
        override fun areItemsTheSame(oldItem: CampusRecruitment, newItem: CampusRecruitment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CampusRecruitment, newItem: CampusRecruitment): Boolean {
            return oldItem == newItem
        }
    }
} 