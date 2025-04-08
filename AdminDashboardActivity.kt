package com.example.placementapp.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.placementapp.MainActivity
import com.example.placementapp.R
import com.example.placementapp.databinding.ActivityAdminPortalBinding
import com.example.placementapp.ui.admin.AdminApplicationsActivity
import com.example.placementapp.ui.admin.AdminProfileActivity
import com.example.placementapp.ui.admin.ManageJobsActivity
import com.example.placementapp.ui.admin.ManageCampusRecruitmentsActivity
import com.example.placementapp.ui.admin.ManageStudentsActivity
import com.example.placementapp.ui.admin.ManageAnnouncementsActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AdminDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminPortalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Initialize Firebase instances first
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
            storage = FirebaseStorage.getInstance()

            // Check if user is logged in
            if (auth.currentUser == null) {
                Log.e("AdminDashboard", "No user logged in")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return
            }

            // Initialize binding
            binding = ActivityAdminPortalBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Setup UI components
            setupToolbar()
            setupNavigationDrawer()
            setupClickListeners()

            // Register broadcast receiver
            try {
                val filter = IntentFilter("ADMIN_PROFILE_UPDATED")
                registerReceiver(profileUpdateReceiver, filter)
            } catch (e: Exception) {
                Log.e("AdminDashboard", "Error registering receiver: ${e.message}")
            }

            // Load profile data
            loadAdminProfile()
            
        } catch (e: Exception) {
            Log.e("AdminDashboard", "Critical error in onCreate: ${e.message}")
            e.printStackTrace()
            try {
                Toast.makeText(this, "Error initializing dashboard. Please try again.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
            } catch (e2: Exception) {
                Log.e("AdminDashboard", "Error handling critical error: ${e2.message}")
            } finally {
                finish()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Admin Dashboard"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, AdminProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupNavigationDrawer() {
        try {
            val toggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.open_nav,
                R.string.close_nav
            )
            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            binding.navigationView.setNavigationItemSelectedListener(this)
        } catch (e: Exception) {
            Log.e("AdminDashboard", "Error setting up navigation drawer: ${e.message}")
        }
    }

    private fun loadAdminProfile() {
        val user = auth.currentUser
        if (user == null) {
            Log.e("AdminDashboard", "No user logged in during profile load")
            return
        }

        try {
            // Set default values first
            binding.adminName.text = "Admin"
            binding.adminRole.text = "Administrator"
            
            val headerView = binding.navigationView.getHeaderView(0)
            headerView?.let { header ->
                header.findViewById<TextView>(R.id.navHeaderName)?.text = "Admin"
                header.findViewById<TextView>(R.id.navHeaderEmail)?.text = user.email ?: ""
            }

            // Load profile data from Firestore
            db.collection("admins").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    try {
                        if (document.exists()) {
                            document.getString("fullName")?.let { fullName ->
                                binding.adminName.text = fullName
                                headerView?.findViewById<TextView>(R.id.navHeaderName)?.text = fullName
                            }

                            document.getString("designation")?.let { designation ->
                                binding.adminRole.text = designation
                            }

                            document.getString("profileImageUrl")?.let { imageUrl ->
                                if (imageUrl.isNotEmpty()) {
                                    updateProfileImages(imageUrl)
                                }
                            }
                        } else {
                            // Create default admin document
                            val defaultData = hashMapOf(
                                "fullName" to "Admin",
                                "email" to user.email,
                                "designation" to "Administrator"
                            )
                            db.collection("admins").document(user.uid)
                                .set(defaultData)
                                .addOnFailureListener { e ->
                                    Log.e("AdminDashboard", "Error creating default admin document: ${e.message}")
                                }
                        }
                    } catch (e: Exception) {
                        Log.e("AdminDashboard", "Error processing admin data: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AdminDashboard", "Error loading admin profile: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("AdminDashboard", "Error in loadAdminProfile: ${e.message}")
        }
    }

    private fun updateProfileImages(imageUrl: String) {
        try {
            // Update main profile image
            Glide.with(applicationContext)
                .load(imageUrl)
                .placeholder(R.drawable.default_profile_image)
                .error(R.drawable.default_profile_image)
                .into(binding.adminProfileImage)

            // Update navigation drawer image
            val headerView = binding.navigationView.getHeaderView(0)
            val navHeaderImage = headerView.findViewById<ImageView>(R.id.navHeaderImage)
            Glide.with(applicationContext)
                .load(imageUrl)
                .placeholder(R.drawable.default_profile_image)
                .error(R.drawable.default_profile_image)
                .into(navHeaderImage)
        } catch (e: Exception) {
            Log.e("AdminDashboard", "Error updating profile images: ${e.message}")
        }
    }

    private fun setupClickListeners() {
        // Manage Jobs Card
        binding.manageJobsCard.setOnClickListener {
            startActivity(Intent(this, ManageJobsActivity::class.java))
        }

        // Student Management Card
        binding.studentManagementCard.setOnClickListener {
            startActivity(Intent(this, ManageStudentsActivity::class.java))
        }

        // Student Applications Card
        binding.studentApplicationsCard.setOnClickListener {
            startActivity(Intent(this, AdminApplicationsActivity::class.java))
        }

        // Campus Recruitments Card
        binding.campusRecruitmentsCard.setOnClickListener {
            startActivity(Intent(this, ManageCampusRecruitmentsActivity::class.java))
        }

        // Post Announcement Card
        binding.postAnnouncementCard.setOnClickListener {
            startActivity(Intent(this, ManageAnnouncementsActivity::class.java))
        }

        // Reports Card
        binding.reportsCard.setOnClickListener {
            showMessage("Reports feature coming soon")
        }

        // Profile Image Click
        binding.adminProfileImage.setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                // Already on dashboard
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, AdminProfileActivity::class.java))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_manage_jobs -> {
                startActivity(Intent(this, ManageJobsActivity::class.java))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_applications -> {
                startActivity(Intent(this, AdminApplicationsActivity::class.java))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_campus_recruitments -> {
                startActivity(Intent(this, ManageCampusRecruitmentsActivity::class.java))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_logout -> {
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            else -> return false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
        return true
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private val profileUpdateReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
            try {
                if (intent?.action == "ADMIN_PROFILE_UPDATED") {
                    val imageUrl = intent.getStringExtra("profileImageUrl")
                    val fullName = intent.getStringExtra("fullName")
                    val email = intent.getStringExtra("email")

                    if (!imageUrl.isNullOrEmpty()) {
                        updateProfileImages(imageUrl)
                    }

                    if (!fullName.isNullOrEmpty()) {
                        binding.adminName.text = fullName
                        val headerView = binding.navigationView.getHeaderView(0)
                        headerView.findViewById<TextView>(R.id.navHeaderName).text = fullName
                    }

                    if (!email.isNullOrEmpty()) {
                        val headerView = binding.navigationView.getHeaderView(0)
                        headerView.findViewById<TextView>(R.id.navHeaderEmail).text = email
                    }

                    // Reload full profile data to ensure consistency
                    loadAdminProfile()
                }
            } catch (e: Exception) {
                Log.e("AdminDashboard", "Error in profile update receiver: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(profileUpdateReceiver)
        } catch (e: Exception) {
            // Ignore if receiver wasn't registered
        }
    }
} 