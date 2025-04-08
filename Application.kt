package com.example.placementapp.models

import com.google.firebase.Timestamp

data class Application(
    var id: String = "",
    val userId: String = "",
    val jobId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val resume: String = "",
    val status: String = "pending",
    val timestamp: Timestamp? = null
) 