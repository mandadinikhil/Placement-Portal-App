package com.example.placementapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placementapp.adapters.JobAdapter
import com.example.placementapp.databinding.ActivityJobsBinding
import com.example.placementapp.models.Job
import com.example.placementapp.ui.admin.EditJobActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EditJobsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobsBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var jobAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Jobs"

        setupRecyclerView()
        loadJobs()
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter { job ->
            val intent = Intent(this, EditJobActivity::class.java).apply {
                putExtra("jobId", job.id)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditJobsActivity)
            adapter = jobAdapter
        }
    }

    private fun loadJobs() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyView.visibility = View.GONE

        db.collection("jobs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                if (documents.isEmpty) {
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    val jobs = documents.mapNotNull { doc ->
                        doc.toObject(Job::class.java).apply { id = doc.id }
                    }
                    jobAdapter.submitList(jobs)
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                Toast.makeText(this, "Error loading jobs: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 