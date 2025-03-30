
package com.example.csiapp_2.seeker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class AppliedJob(
    val id: String,
    val company: String,
    val title: String,
    val status: String
)

class SeekerAppliedJobsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppliedJobsScreen()
        }
    }
}

@Composable
fun AppliedJobsScreen() {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var userEmail by remember { mutableStateOf<String?>(null) }
    var appliedJobs by remember { mutableStateOf<List<AppliedJob>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    // Step 1: Get user email
    LaunchedEffect(userId) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val email = doc.getString("email")
                if (email != null) {
                    userEmail = email
                } else {
                    error = "User email not found."
                }
            }
            .addOnFailureListener { error = it.message }
    }

    // Step 2: Get applied jobs
    LaunchedEffect(userEmail) {
        if (userEmail != null) {
            db.collection("job_posts")
                .whereArrayContains("applicants", userEmail!!)
                .get()
                .addOnSuccessListener { jobsSnapshot ->
                    val tempJobs = mutableListOf<AppliedJob>()
                    jobsSnapshot.forEach { jobDoc ->
                        val jobId = jobDoc.id
                        val company = jobDoc.getString("company") ?: "Unknown"
                        val title = jobDoc.getString("title") ?: "Untitled"

                        // Step 3: Get application status
                        db.collection("applications")
                            .whereEqualTo("job_id", jobId)
                            .whereEqualTo("seeker_id", userId)
                            .get()
                            .addOnSuccessListener { appsSnapshot ->
                                val status = appsSnapshot.firstOrNull()?.getString("status") ?: "pending"

                                tempJobs.add(
                                    AppliedJob(
                                        id = jobId,
                                        company = company,
                                        title = title,
                                        status = status
                                    )
                                )
                                appliedJobs = tempJobs.toList()
                            }
                    }
                }
                .addOnFailureListener { error = it.message }
        }
    }

    Scaffold { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("My Applications", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            } else if (appliedJobs.isEmpty()) {
                Text("No applications found.")
            } else {
                LazyColumn {
                    items(appliedJobs) { job ->
                        val context = LocalContext.current
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    // Navigate to job details
                                    val intent = Intent(context, SeekerViewJobDetailsActivity::class.java)
                                    intent.putExtra("jobId", job.id)
                                    context.startActivity(intent)
                                }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(text = job.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Company: ${job.company}")
                                Text(text = "Status: ${job.status}")
                            }
                        }
                    }
                }
            }
        }
    }
}
