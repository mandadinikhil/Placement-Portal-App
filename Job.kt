package com.example.placementapp.models

import com.google.firebase.Timestamp
import java.util.Date

data class Job(
    var id: String = "",
    val title: String = "",
    val companyName: String = "",
    val description: String = "",
    val requirements: String = "",
    val location: String = "",
    val salary: String = "",
    val lastDate: String = "",
    val status: String = "active",
    val timestamp: Timestamp = Timestamp.now(),
    val postedDate: Timestamp = Timestamp.now(),
    var applicationCount: Int = 0
) {
    // Compatibility methods for old code
    val jobTitle: String get() = title
    val jobDescription: String get() = description

    fun getDisplayTitle(): String = title.ifEmpty { jobTitle }
} 