package com.example.placementapp.models

import com.google.firebase.Timestamp

data class CampusRecruitment(
    val id: String = "",
    val companyName: String = "",
    val jobRole: String = "",
    val description: String = "",
    val eligibilityCriteria: String = "",
    val driveDate: Timestamp = Timestamp.now(),
    val lastDateToApply: Timestamp = Timestamp.now(),
    val venue: String = "",
    val package_: String = "",
    val requiredSkills: String = "",
    val status: String = "active",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "id" to id,
            "companyName" to companyName,
            "jobRole" to jobRole,
            "description" to description,
            "eligibilityCriteria" to eligibilityCriteria,
            "driveDate" to driveDate,
            "lastDateToApply" to lastDateToApply,
            "venue" to venue,
            "package" to package_,
            "requiredSkills" to requiredSkills,
            "status" to status,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 