package com.example.placementapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.placementapp.R
import com.example.placementapp.databinding.ActivityLoginBinding
import com.example.placementapp.ui.admin.AdminDashboardActivity
import com.example.placementapp.ui.StudentDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            checkUserTypeAndRedirect(auth.currentUser?.uid ?: return)
            return
        }

        // Create default admin account if it doesn't exist
        createDefaultAdminIfNotExists()

        setupClickListeners()
    }
  
    private fun setupClickListeners() {
        // Register button click
        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Login button click
        binding.loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun createDefaultAdminIfNotExists() {
        val adminEmail = "admin@cutmap.ac.in"
        val adminPassword = "admin123"

        // First try to sign in with admin credentials
        auth.signInWithEmailAndPassword(adminEmail, adminPassword)
            .addOnSuccessListener { authResult ->
                // Admin exists in Authentication, check/create Firestore document
                val user = authResult.user
                if (user != null) {
                    db.collection("users").document(user.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                // Create admin document
                                val adminData = hashMapOf(
                                    "email" to adminEmail,
                                    "role" to "admin",
                                    "name" to "Admin",
                                    "uid" to user.uid
                                )
                                db.collection("users").document(user.uid)
                                    .set(adminData)
                                    .addOnSuccessListener {
                                        println("Admin document created")
                                    }
                            }
                            // Always sign out after checking
                            auth.signOut()
                        }
                }
            }
            .addOnFailureListener {
                // Admin doesn't exist, create it
                auth.createUserWithEmailAndPassword(adminEmail, adminPassword)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            val adminData = hashMapOf(
                                "email" to adminEmail,
                                "role" to "admin",
                                "name" to "Admin",
                                "uid" to user.uid
                            )
                            db.collection("users").document(user.uid)
                                .set(adminData)
                                .addOnSuccessListener {
                                    println("Admin account created")
                                    auth.signOut()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        println("Error creating admin: ${e.message}")
                    }
            }
    }

    private fun loginUser() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        showProgress()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    checkUserTypeAndRedirect(userId)
                } else {
                    hideProgress()
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                hideProgress()
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Login failed", e)
            }
    }

    private fun checkUserTypeAndRedirect(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")?.lowercase() ?: ""
                    hideProgress()
                    
                    when (role) {
                        "admin" -> {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                            finish()
                        }
                        "student" -> {
                            startActivity(Intent(this, StudentDashboardActivity::class.java))
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show()
                            auth.signOut()
                        }
                    }
                } else {
                    hideProgress()
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                }
            }
            .addOnFailureListener { e ->
                hideProgress()
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Error checking user type", e)
                auth.signOut()
            }
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginButton.isEnabled = false
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
        binding.loginButton.isEnabled = true
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showError(message: String) {
        showProgress()
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
} 