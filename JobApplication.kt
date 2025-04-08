package com.example.placementapp.models

import com.google.firebase.Timestamp

data class JobApplication(
    var id: String = "",
    val studentId: String = "",
    val jobId: String = "",
    val companyName: String = "",
    val jobRole: String = "",
    val status: String = "pending",
    val appliedAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) 