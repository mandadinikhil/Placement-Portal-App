package com.example.placementapp

import android.app.Application
import com.google.firebase.FirebaseApp

class PlacementApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
} 