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

class SeekerViewJobDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jobId = intent.getStringExtra("jobId") ?: ""

        setContent {
            SeekerViewJobDetailsScreen(jobId)
        }
    }
}


@Composable
fun SeekerViewJobDetailsScreen(jobId: String) {
    val db = FirebaseFirestore.getInstance()
    var jobData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(jobId) {
        db.collection("job_posts").document(jobId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    jobData = doc.data
                } else {
                    error = "Job not found"
                }
            }
            .addOnFailureListener {
                error = it.message
            }
    }

    Scaffold { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            } else if (jobData == null) {
                CircularProgressIndicator()
            } else {
                Text("Job Details", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Text("Title: ${jobData?.get("title")}")
                Text("Company: ${jobData?.get("company")}")
                Text("Description: ${jobData?.get("description")}")
                Text("Location: ${jobData?.get("location")}")
                Text("Salary: ${jobData?.get("salary")}")
                Text("Posted On: ${jobData?.get("posted_on")}")
            }
        }
    }
}
