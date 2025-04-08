package com.example.placementapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.placementapp.MainActivity
import com.example.placementapp.R
import com.example.placementapp.databinding.ActivityStudentPortalBinding
import com.example.placementapp.ui.student.StudentCampusRecruitmentsActivity
import com.example.placementapp.ui.student.StudentProfileActivity
import com.example.placementapp.ui.student.jobs.ViewJobsActivity
import com.example.placementapp.ui.student.MyApplicationsActivity
import com.example.placementapp.ui.LoginActivity
import com.example.placementapp.ui.SettingsActivity
import com.example.placementapp.ui.student.StudentAnnouncementsActivity
import com.example.placementapp.ui.student.InterviewTipsActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class StudentDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityStudentPortalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var toggle: ActionBarDrawerToggle

    private val PERMISSION_CODE = 1001
    private val IMAGE_PICK_CODE = 1002

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Add broadcast receiver for profile image updates
    private var profileUpdateReceiver: android.content.BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentPortalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize broadcast receiver
        profileUpdateReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                try {
                    if (intent?.action == "PROFILE_IMAGE_UPDATED") {
                        val imageUrl = intent.getStringExtra("profileImageUrl")
                        Log.d("StudentDashboard", "Received profile update broadcast with URL: $imageUrl")
                        imageUrl?.let {
                            updateProfileImage(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("StudentDashboard", "Error in broadcast receiver: ${e.message}")
                }
            }
        }

        setupToolbar()
        setupNavigationDrawer()
        setupDashboardClickListeners()
        setupProfileImageClick()
        loadStudentData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Student Portal"
    }

    private fun setupNavigationDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupDashboardClickListeners() {
        binding.apply {
            viewJobsCard.setOnClickListener {
                startActivity(Intent(this@StudentDashboardActivity, ViewJobsActivity::class.java))
            }

            myApplicationsCard.setOnClickListener {
                startActivity(Intent(this@StudentDashboardActivity, MyApplicationsActivity::class.java))
            }

            announcementsCard.setOnClickListener {
                startActivity(Intent(this@StudentDashboardActivity, StudentAnnouncementsActivity::class.java))
            }

            editCvCard.setOnClickListener {
                Toast.makeText(this@StudentDashboardActivity, "Statistics coming soon", Toast.LENGTH_SHORT).show()
            }

            campusRecruitersCard.setOnClickListener {
                startActivity(Intent(this@StudentDashboardActivity, StudentCampusRecruitmentsActivity::class.java))
            }

            interviewTipsCard.setOnClickListener {
                startActivity(Intent(this@StudentDashboardActivity, InterviewTipsActivity::class.java))
            }
        }
    }

    private fun setupProfileImageClick() {
        binding.studentProfileImage.setOnClickListener {
            checkPermissionAndPickImage()
        }
    }

    private fun checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, use READ_MEDIA_IMAGES
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openImagePicker()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            // For Android 12 and below, use READ_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openImagePicker()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadProfileImage(uri)
            }
        }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val imageRef = storage.reference.child("profile_images/$userId.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Update profile image URL in Firestore
                    db.collection("users").document(userId)
                        .update("profileImage", downloadUrl.toString())
                        .addOnSuccessListener {
                            // Update UI with new image
                            Glide.with(this)
                                .load(imageUri)
                                .placeholder(R.drawable.default_profile_image)
                                .into(binding.studentProfileImage)

                            // Update nav header image
                            val headerView = binding.navView.getHeaderView(0)
                            Glide.with(this)
                                .load(imageUri)
                                .placeholder(R.drawable.default_profile_image)
                                .into(headerView.findViewById(R.id.navHeaderImage))

                            Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to update profile image: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadStudentData() {
        val userId = auth.currentUser?.uid ?: return
        val headerView = binding.navView.getHeaderView(0)

        db.collection("students").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Update profile image in both main view and nav header
                    try {
                        val profileImageUrl = document.getString("profileImageUrl")
                        Log.d("StudentDashboard", "Loading profile image URL: $profileImageUrl")
                        
                        if (!profileImageUrl.isNullOrEmpty()) {
                            // Load image in main view
                            Glide.with(this)
                                .load(profileImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .into(binding.studentProfileImage)

                            // Load image in nav header
                            val headerImage = headerView.findViewById<CircleImageView>(R.id.navHeaderImage)
                            Glide.with(this)
                                .load(profileImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .into(headerImage)
                        } else {
                            Log.d("StudentDashboard", "No profile image URL found")
                            // Load default image in main view
                            Glide.with(this)
                                .load(R.drawable.default_profile_image)
                                .into(binding.studentProfileImage)
                            
                            // Load default image in nav header
                            val headerImage = headerView.findViewById<CircleImageView>(R.id.navHeaderImage)
                            Glide.with(this)
                                .load(R.drawable.default_profile_image)
                                .into(headerImage)
                        }
                    } catch (e: Exception) {
                        Log.e("StudentDashboard", "Error loading profile image: ${e.message}")
                        e.printStackTrace()
                    }

                    // Update name and other info
                    document.getString("fullName")?.let { name ->
                        binding.studentName.text = name
                        headerView.findViewById<TextView>(R.id.navHeaderName)?.text = name
                    }
                    document.getString("registrationNumber")?.let { regNo ->
                        binding.studentId.text = regNo
                        headerView.findViewById<TextView>(R.id.navHeaderEmail)?.text = regNo
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load student data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProfileImage(imageUrl: String) {
        try {
            if (!isFinishing) {
                Log.d("StudentDashboard", "Updating profile image with URL: $imageUrl")
                // Update main profile image
                Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(binding.studentProfileImage)

                // Update nav header image
                val headerView = binding.navView.getHeaderView(0)
                val headerImage = headerView.findViewById<CircleImageView>(R.id.navHeaderImage)
                Glide.with(this)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(headerImage)
            }
        } catch (e: Exception) {
            Log.e("StudentDashboard", "Error updating profile image: ${e.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            profileUpdateReceiver?.let { receiver ->
                val filter = android.content.IntentFilter("PROFILE_IMAGE_UPDATED")
                try {
                    registerReceiver(receiver, filter)
                } catch (e: Exception) {
                    Log.e("StudentDashboard", "Error registering receiver: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("StudentDashboard", "Error in onStart: ${e.message}")
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            profileUpdateReceiver?.let { receiver ->
                try {
                    unregisterReceiver(receiver)
                } catch (e: IllegalArgumentException) {
                    // Receiver was not registered or already unregistered
                    Log.e("StudentDashboard", "Receiver not registered or already unregistered: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("StudentDashboard", "Error in onStop: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            profileUpdateReceiver?.let { receiver ->
                try {
                    unregisterReceiver(receiver)
                } catch (e: IllegalArgumentException) {
                    // Receiver was not registered or already unregistered
                }
            }
            profileUpdateReceiver = null
        } catch (e: Exception) {
            Log.e("StudentDashboard", "Error in onDestroy: ${e.message}")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, StudentProfileActivity::class.java))
            }
            R.id.nav_jobs -> {
                startActivity(Intent(this, ViewJobsActivity::class.java))
            }
            R.id.nav_applications -> {
                startActivity(Intent(this, MyApplicationsActivity::class.java))
            }
            R.id.nav_notifications -> {
                Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_recruiters -> {
                startActivity(Intent(this, StudentCampusRecruitmentsActivity::class.java))
            }
            R.id.nav_logout -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
        return true
    }
} 