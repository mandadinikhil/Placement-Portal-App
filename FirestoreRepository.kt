package com.example.placementapp.repository

import com.example.placementapp.models.Admin
import com.example.placementapp.models.Job
import com.example.placementapp.models.JobApplication
import com.example.placementapp.models.Student
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val studentsCollection = firestore.collection("students")
    private val adminsCollection = firestore.collection("admins")

    // Student Operations
    suspend fun createStudent(student: Student) {
        studentsCollection.document(student.uid)
            .set(student)
            .await()
    }

    suspend fun getStudent(uid: String): Student? {
        return try {
            val doc = studentsCollection.document(uid).get().await()
            if (doc.exists()) {
                doc.toObject(Student::class.java)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateStudent(student: Student) {
        studentsCollection.document(student.uid)
            .set(student)
            .await()
    }

    suspend fun getAllStudents(department: String? = null): List<Student> {
        return try {
            val query = if (department != null) {
                studentsCollection
                    .whereEqualTo("department", department)
                    .whereEqualTo("status", "active")
            } else {
                studentsCollection
                    .whereEqualTo("status", "active")
            }

            query.orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { doc -> 
                    doc.data?.let { Student.fromMap(it) }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Admin Operations
    suspend fun createAdmin(admin: Admin) {
        adminsCollection.document(admin.uid)
            .set(admin.toMap())
            .await()
    }

    suspend fun getAdmin(uid: String): Admin? {
        return try {
            val doc = adminsCollection.document(uid).get().await()
            if (doc.exists()) {
                Admin.fromMap(doc.data as Map<String, Any>)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateAdmin(admin: Admin) {
        adminsCollection.document(admin.uid)
            .update(admin.toMap())
            .await()
    }

    suspend fun getAllAdmins(department: String? = null): List<Admin> {
        return try {
            val query = if (department != null) {
                adminsCollection
                    .whereEqualTo("department", department)
                    .whereEqualTo("status", "active")
            } else {
                adminsCollection
                    .whereEqualTo("status", "active")
            }

            query.orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { doc -> 
                    doc.data?.let { Admin.fromMap(it) }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Example of how to use batch operations for multiple updates
    suspend fun updateMultipleStudents(students: List<Student>) {
        val batch = firestore.batch()
        
        students.forEach { student ->
            val docRef = studentsCollection.document(student.uid)
            batch.update(docRef, student.toMap())
        }

        batch.commit().await()
    }

    // Example of how to use transactions for atomic operations
    suspend fun updateStudentCGPA(uid: String, newCGPA: Double) {
        firestore.runTransaction { transaction ->
            val docRef = studentsCollection.document(uid)
            val snapshot = transaction.get(docRef)
            
            if (snapshot.exists()) {
                transaction.update(docRef, "cgpa", newCGPA)
                transaction.update(docRef, "updatedAt", com.google.firebase.Timestamp.now())
            }
        }.await()
    }

    suspend fun getStudentProfile(studentId: String): Result<Student> {
        return try {
            val document = firestore.collection("students")
                .document(studentId)
                .get()
                .await()

            if (document.exists()) {
                Result.success(document.toObject(Student::class.java)!!)
            } else {
                Result.failure(Exception("Student not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudentProfile(studentId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("students")
                .document(studentId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJobs(): Result<List<Job>> {
        return try {
            val querySnapshot = firestore.collection("jobs")
                .whereEqualTo("status", "active")
                .get()
                .await()

            val jobs = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.apply {
                    id = doc.id
                }
            }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJobApplications(studentId: String): Result<List<JobApplication>> {
        return try {
            val querySnapshot = firestore.collection("applications")
                .whereEqualTo("studentId", studentId)
                .get()
                .await()

            val applications = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(JobApplication::class.java)?.apply {
                    id = doc.id
                }
            }
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun applyForJob(application: JobApplication): Result<String> {
        return try {
            val docRef = firestore.collection("applications").document()
            application.id = docRef.id
            docRef.set(application).await()
            Result.success(application.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJobApplication(applicationId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("applications")
                .document(applicationId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 