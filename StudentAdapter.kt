package com.example.placementapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.placementapp.R
import com.example.placementapp.databinding.ItemStudentBinding
import com.example.placementapp.models.Student

class StudentAdapter(
    private val students: List<Student>,
    private val onViewProfileClick: (Student) -> Unit,
    private val onViewCVClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount() = students.size

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.apply {
                // Load profile image
                Glide.with(studentImage)
                    .load(student.profileImageUrl.ifEmpty { student.profileImagePath })
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .circleCrop()
                    .into(studentImage)

                // Basic Information
                tvStudentName.text = student.fullName
                tvRegNo.text = "Reg No: ${student.registrationNumber}"
                tvBranch.text = "Branch: ${student.branch}"
                if (student.department.isNotEmpty()) {
                    tvDepartment.text = "Department: ${student.department}"
                    tvDepartment.isVisible = true
                } else {
                    tvDepartment.isVisible = false
                }

                // Contact Information
                tvPersonalEmail.text = "Personal Email: ${student.personalEmail}"
                tvCollegeEmail.text = "College Email: ${student.collegeEmail}"
                tvContactNumber.text = "Contact: ${student.contactNumber}"
                if (student.phone.isNotEmpty() && student.phone != student.contactNumber) {
                    tvPhone.text = "Alt. Phone: ${student.phone}"
                    tvPhone.isVisible = true
                } else {
                    tvPhone.isVisible = false
                }

                // Academic Information
                tvTenthPercentage.text = "10th: ${student.tenthPercentage}%"
                tvInterPercentage.text = "12th: ${student.interPercentage}%"
                tvLastSemesterCGPA.text = "Last Sem CGPA: ${student.lastSemesterCGPA}"
                tvCurrentSGPA.text = "Current SGPA: ${student.currentSGPA}"
                tvCurrentSemester.text = "Semester: ${student.currentSemester}"
                if (student.cgpa.isNotEmpty()) {
                    tvCGPA.text = "Overall CGPA: ${student.cgpa}"
                    tvCGPA.isVisible = true
                } else {
                    tvCGPA.isVisible = false
                }
                if (student.skills.isNotEmpty()) {
                    tvSkills.text = "Skills: ${student.skills}"
                    tvSkills.isVisible = true
                } else {
                    tvSkills.isVisible = false
                }

                // Family Information
                tvFatherName.text = "Father's Name: ${student.fatherName}"
                tvMotherName.text = "Mother's Name: ${student.motherName}"
                tvPermanentAddress.text = "Address: ${student.permanentAddress}"

                // Placement Information
                tvPlacementInterest.text = "Interested in Placement: ${if (student.interestedInPlacement) "Yes" else "No"}"
                if (student.interestedDomain.isNotEmpty()) {
                    tvInterestedDomain.text = "Interested Domain: ${student.interestedDomain}"
                    tvInterestedDomain.isVisible = true
                } else {
                    tvInterestedDomain.isVisible = false
                }

                // Document Status
                val cvStatus = when {
                    student.cvUrl.isNotEmpty() -> "CV: Uploaded ✓"
                    student.cvPath.isNotEmpty() -> "CV: Uploaded ✓"
                    else -> "CV: Not Uploaded ✗"
                }
                tvCVStatus.text = cvStatus

                val letterStatus = when {
                    !student.interestedInPlacement && student.concernedLetterUrl.isEmpty() -> 
                        "Consent Letter Required !"
                    student.concernedLetterUrl.isNotEmpty() -> 
                        "Consent Letter: Uploaded ✓"
                    else -> "Consent Letter: Not Required"
                }
                tvConsentLetterStatus.text = letterStatus

                // Verification Status
                tvVerificationStatus.text = if (student.isEmailVerified) 
                    "Email Verified ✓" else "Email Not Verified ✗"

                // Button Click Listeners
                btnViewProfile.setOnClickListener { onViewProfileClick(student) }
                btnViewCV.setOnClickListener { onViewCVClick(student) }
            }
        }
    }
} 