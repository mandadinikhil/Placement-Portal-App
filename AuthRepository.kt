package com.example.placementapp.repository

import com.example.placementapp.models.Admin
import com.example.placementapp.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

sealed class AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
}

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val studentsCollection = firestore.collection("students")
    private val adminsCollection = firestore.collection("admins")

    suspend fun signUpStudent(
        email: String,
        password: String,
        student: Student
    ): AuthResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            
            // Create student document in Firestore
            val studentWithUid = student.copy(
                uid = user.uid,
                collegeEmail = email,
                isEmailVerified = false
            )
            
            studentsCollection.document(user.uid)
                .set(studentWithUid.toMap())
                .await()
            
            // Send email verification
            user.sendEmailVerification().await()
            
            AuthResult.Success(user)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult.Error(e)
        }
    }

    suspend fun signUpAdmin(
        email: String,
        password: String,
        admin: Admin
    ): AuthResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            
            // Create admin document in Firestore
            val adminWithUid = admin.copy(
                uid = user.uid,
                collegeEmail = email,
                isEmailVerified = false
            )
            
            adminsCollection.document(user.uid)
                .set(adminWithUid.toMap())
                .await()
            
            // Send email verification
            user.sendEmailVerification().await()
            
            AuthResult.Success(user)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult.Error(e)
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Sign in failed")
            AuthResult.Success(user)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AuthResult.Error(e)
        }
    }

    suspend fun isUserStudent(uid: String): Boolean {
        return try {
            val doc = studentsCollection.document(uid).get().await()
            doc.exists()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun isUserAdmin(uid: String): Boolean {
        return try {
            val doc = adminsCollection.document(uid).get().await()
            doc.exists()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }

    suspend fun getCurrentStudent(): Student? {
        val user = auth.currentUser ?: return null
        return try {
            val doc = studentsCollection.document(user.uid).get().await()
            if (doc.exists()) {
                doc.toObject(Student::class.java)
            } else null
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }

    suspend fun getCurrentAdmin(): Admin? {
        val user = auth.currentUser ?: return null
        return try {
            val doc = adminsCollection.document(user.uid).get().await()
            if (doc.exists()) {
                doc.toObject(Admin::class.java)
            } else null
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            false
        }
    }
} 