package com.example.placementapp.models

data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val createdByName: String = "",
    val priority: String = "normal", // normal, high, urgent
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "content" to content,
            "timestamp" to timestamp,
            "createdBy" to createdBy,
            "createdByName" to createdByName,
            "priority" to priority,
            "isActive" to isActive
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any>): Announcement {
            return Announcement(
                id = id,
                title = map["title"] as? String ?: "",
                content = map["content"] as? String ?: "",
                timestamp = map["timestamp"] as? Long ?: System.currentTimeMillis(),
                createdBy = map["createdBy"] as? String ?: "",
                createdByName = map["createdByName"] as? String ?: "",
                priority = map["priority"] as? String ?: "normal",
                isActive = map["isActive"] as? Boolean ?: true
            )
        }
    }
} 