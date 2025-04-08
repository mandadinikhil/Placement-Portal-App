package com.example.placementapp.models

import com.google.firebase.Timestamp

data class Admin(
    val uid: String = "",
    val fullName: String = "",
    val employeeId: String = "",
    val personalEmail: String = "",
    val collegeEmail: String = "",
    val contactNumber: String = "",
    val department: String = "",
    val designation: String = "",
    val profileImage: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isEmailVerified: Boolean = false,
    val status: String = "active",
    val permissions: AdminPermissions = AdminPermissions()
) {
    // Convert Admin object to HashMap for Firestore
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "fullName" to fullName,
        "employeeId" to employeeId,
        "personalEmail" to personalEmail,
        "collegeEmail" to collegeEmail,
        "contactNumber" to contactNumber,
        "department" to department,
        "designation" to designation,
        "profileImage" to profileImage,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "isEmailVerified" to isEmailVerified,
        "status" to status,
        "permissions" to permissions.toMap()
    )

    companion object {
        // Convert Firestore document to Admin object
        fun fromMap(map: Map<String, Any>): Admin {
            return Admin(
                uid = map["uid"] as? String ?: "",
                fullName = map["fullName"] as? String ?: "",
                employeeId = map["employeeId"] as? String ?: "",
                personalEmail = map["personalEmail"] as? String ?: "",
                collegeEmail = map["collegeEmail"] as? String ?: "",
                contactNumber = map["contactNumber"] as? String ?: "",
                department = map["department"] as? String ?: "",
                designation = map["designation"] as? String ?: "",
                profileImage = map["profileImage"] as? String ?: "",
                createdAt = map["createdAt"] as? Timestamp ?: Timestamp.now(),
                updatedAt = map["updatedAt"] as? Timestamp ?: Timestamp.now(),
                isEmailVerified = map["isEmailVerified"] as? Boolean ?: false,
                status = map["status"] as? String ?: "active",
                permissions = AdminPermissions.fromMap(map["permissions"] as? Map<String, Any> ?: mapOf())
            )
        }
    }
}

data class AdminPermissions(
    val canPostJobs: Boolean = false,
    val canManageStudents: Boolean = false,
    val canManageApplications: Boolean = false,
    val isSuperAdmin: Boolean = false
) {
    fun toMap(): Map<String, Boolean> = mapOf(
        "canPostJobs" to canPostJobs,
        "canManageStudents" to canManageStudents,
        "canManageApplications" to canManageApplications,
        "isSuperAdmin" to isSuperAdmin
    )

    companion object {
        fun fromMap(map: Map<String, Any>): AdminPermissions {
            return AdminPermissions(
                canPostJobs = map["canPostJobs"] as? Boolean ?: false,
                canManageStudents = map["canManageStudents"] as? Boolean ?: false,
                canManageApplications = map["canManageApplications"] as? Boolean ?: false,
                isSuperAdmin = map["isSuperAdmin"] as? Boolean ?: false
            )
        }
    }
} 