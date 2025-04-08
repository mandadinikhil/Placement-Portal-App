package com.example.placementapp.models

import com.google.firebase.Timestamp

data class CampusRecruitmentApplication(
    var id: String = "",
    val recruitmentId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentEmail: String = "",
    val studentPhone: String = "",
    val studentCGPA: String = "",
    val companyName: String = "",
    val jobRole: String = "",
    val status: String = "pending", // pending, accepted, rejected
    val appliedAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "recruitmentId" to recruitmentId,
            "studentId" to studentId,
            "studentName" to studentName,
            "studentEmail" to studentEmail,
            "studentPhone" to studentPhone,
            "studentCGPA" to studentCGPA,
            "companyName" to companyName,
            "jobRole" to jobRole,
            "status" to status,
            "appliedAt" to appliedAt,
            "updatedAt" to updatedAt
        )
    }
} 