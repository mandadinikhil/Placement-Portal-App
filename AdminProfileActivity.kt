package com.example.placementapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.placementapp.databinding.ActivityAdminProfileBinding

class AdminProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        // Initialize views and set up UI components
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Admin Profile"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
} 