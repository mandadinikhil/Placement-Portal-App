package com.example.placementapp.ui.student

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placementapp.adapters.ApplicationAdapter
import com.example.placementapp.adapters.CampusApplicationAdapter
import com.example.placementapp.databinding.ActivityMyApplicationsBinding
import com.example.placementapp.models.JobApplication
import com.example.placementapp.models.CampusRecruitmentApplication
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyApplicationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyApplicationsBinding
    private lateinit var jobApplicationAdapter: ApplicationAdapter
    private lateinit var campusApplicationAdapter: CampusApplicationAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val campusApplications = mutableListOf<CampusRecruitmentApplication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyApplicationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Applications"

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupApplicationsList()
    }

    private fun setupApplicationsList() {
        // Initialize job applications adapter
        jobApplicationAdapter = ApplicationAdapter()
        binding.recyclerViewJobApplications.apply {
            layoutManager = LinearLayoutManager(this@MyApplicationsActivity)
            adapter = jobApplicationAdapter
        }

        // Initialize campus applications adapter
        campusApplicationAdapter = CampusApplicationAdapter(campusApplications)
        binding.recyclerViewCampusApplications.apply {
            layoutManager = LinearLayoutManager(this@MyApplicationsActivity)
            adapter = campusApplicationAdapter
        }

        // Load both types of applications
        loadApplications()
    }

    private fun loadApplications() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            showMessage("Please log in to view applications")
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.emptyStateJobApplications.visibility = View.GONE
        binding.emptyStateCampusApplications.visibility = View.GONE
        binding.recyclerViewJobApplications.visibility = View.GONE
        binding.recyclerViewCampusApplications.visibility = View.GONE

        // Load Job Applications
        db.collection("applications")
            .whereEqualTo("studentId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val jobApplicationsList = documents.mapNotNull { doc ->
                    try {
                        doc.toObject(JobApplication::class.java).apply {
                            id = doc.id
                        }
                    } catch (e: Exception) {
                        showMessage("Error converting job application: ${e.message}")
                        null
                    }
                }

                jobApplicationAdapter.submitList(jobApplicationsList)
                binding.emptyStateJobApplications.visibility = if (jobApplicationsList.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerViewJobApplications.visibility = if (jobApplicationsList.isEmpty()) View.GONE else View.VISIBLE
            }
            .addOnFailureListener { e ->
                showMessage("Error loading job applications: ${e.message}")
                binding.emptyStateJobApplications.visibility = View.VISIBLE
                binding.recyclerViewJobApplications.visibility = View.GONE
            }

        // Load Campus Recruitment Applications
        db.collection("campus_recruitment_applications")
            .whereEqualTo("studentId", userId)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                
                campusApplications.clear()
                documents.mapNotNull { doc ->
                    try {
                        doc.toObject(CampusRecruitmentApplication::class.java)?.apply {
                            id = doc.id
                        }
                    } catch (e: Exception) {
                        showMessage("Error converting campus application: ${e.message}")
                        null
                    }
                }.let { campusApplications.addAll(it) }

                campusApplicationAdapter.notifyDataSetChanged()
                binding.emptyStateCampusApplications.visibility = if (campusApplications.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerViewCampusApplications.visibility = if (campusApplications.isEmpty()) View.GONE else View.VISIBLE
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                showMessage("Error loading campus applications: ${e.message}")
                binding.emptyStateCampusApplications.visibility = View.VISIBLE
                binding.recyclerViewCampusApplications.visibility = View.GONE
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
} 