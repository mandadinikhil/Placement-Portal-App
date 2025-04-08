package com.example.placementapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.placementapp.databinding.ActivityAddJobBinding
import com.example.placementapp.models.Job
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AddJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddJobBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add New Job"

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveJob()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        with(binding) {
            if (tilJobTitle.editText?.text.toString().trim().isEmpty()) {
                tilJobTitle.error = "Job title is required"
                isValid = false
            } else {
                tilJobTitle.error = null
            }

            if (tilCompanyName.editText?.text.toString().trim().isEmpty()) {
                tilCompanyName.error = "Company name is required"
                isValid = false
            } else {
                tilCompanyName.error = null
            }

            if (tilJobDescription.editText?.text.toString().trim().isEmpty()) {
                tilJobDescription.error = "Job description is required"
                isValid = false
            } else {
                tilJobDescription.error = null
            }

            if (tilRequirements.editText?.text.toString().trim().isEmpty()) {
                tilRequirements.error = "Requirements are required"
                isValid = false
            } else {
                tilRequirements.error = null
            }

            if (tilSalary.editText?.text.toString().trim().isEmpty()) {
                tilSalary.error = "Salary is required"
                isValid = false
            } else {
                tilSalary.error = null
            }

            if (tilLocation.editText?.text.toString().trim().isEmpty()) {
                tilLocation.error = "Location is required"
                isValid = false
            } else {
                tilLocation.error = null
            }

            if (tilLastDate.editText?.text.toString().trim().isEmpty()) {
                tilLastDate.error = "Last date is required"
                isValid = false
            } else {
                tilLastDate.error = null
            }
        }

        return isValid
    }

    private fun saveJob() {
        val timestamp = Timestamp.now()
        val job = Job(
            id = "",
            title = binding.tilJobTitle.editText?.text.toString().trim(),
            companyName = binding.tilCompanyName.editText?.text.toString().trim(),
            description = binding.tilJobDescription.editText?.text.toString().trim(),
            requirements = binding.tilRequirements.editText?.text.toString().trim(),
            salary = binding.tilSalary.editText?.text.toString().trim(),
            location = binding.tilLocation.editText?.text.toString().trim(),
            lastDate = binding.tilLastDate.editText?.text.toString().trim(),
            status = "active",
            timestamp = timestamp,
            postedDate = timestamp
        )

        db.collection("jobs")
            .add(job)
            .addOnSuccessListener {
                Toast.makeText(this, "Job added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding job: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 