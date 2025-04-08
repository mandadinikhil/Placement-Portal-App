package com.example.placementapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.placementapp.databinding.ActivityMainBinding
import com.example.placementapp.ui.AdminDashboardActivity
import com.example.placementapp.ui.LoginActivity
import com.example.placementapp.ui.StudentDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            redirectToAppropriateScreen()
        }

        // Set up click listener for the Get Started button
        binding.btnGetStarted.setOnClickListener {
            if (auth.currentUser != null) {
                redirectToAppropriateScreen()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun redirectToAppropriateScreen() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Show a loading message
            Toast.makeText(this, "Loading your dashboard...", Toast.LENGTH_SHORT).show()
            
            // Get user role from Firestore
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        when (document.getString("role")?.lowercase()) {
                            "student" -> {
                                startActivity(Intent(this, StudentDashboardActivity::class.java))
                                finish()
                            }
                            "admin" -> {
                                startActivity(Intent(this, AdminDashboardActivity::class.java))
                                finish()
                            }
                            else -> {
                                // Invalid or no role, logout and redirect to login
                                auth.signOut()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                                Toast.makeText(this, "Invalid user role. Please login again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Document doesn't exist, logout and redirect to login
                        auth.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                        Toast.makeText(this, "User data not found. Please login again.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Error getting user data, logout and redirect to login
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No user ID, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}