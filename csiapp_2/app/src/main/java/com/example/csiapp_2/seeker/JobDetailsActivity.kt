package com.example.csiapp_2.seeker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class JobDetails(
    val title: String = "",
    val company: String = "",
    val salary: String = "",
    val location: String = "",
    val description: String = "",
    val skills_required: List<String> = emptyList()
)

class JobDetailsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jobId = intent.getStringExtra("jobId") ?: ""

        setContent {
            JobDetailsScreen(jobId = jobId)
        }
    }
}

@Composable
fun JobDetailsScreen(jobId: String) {

    val context = LocalContext.current  // Get context for navigation

    var job by remember { mutableStateOf<JobDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }

//    // Fetch Job Details
//    LaunchedEffect(jobId) {
//        if (jobId.isNotEmpty()) {
//            FirebaseFirestore.getInstance()
//                .collection("job_posts")
//                .document(jobId)
//                .get()
//                .addOnSuccessListener { doc ->
//                    doc?.toObject(JobDetails::class.java)?.let {
//                        job = it
//                    }
//                    isLoading = false
//                }
//                .addOnFailureListener {
//                    isLoading = false
//                }
//        } else {
//            isLoading = false
//        }
//    }

    //mocking
    // Mock data for testing
    job = JobDetails(
        title = "Software Engineer",
        company = "Mock Corp",
        salary = "$100k - $120k",
        location = "Remote",
        description = "Develop and maintain cutting-edge applications.",
        skills_required = listOf("Kotlin", "Jetpack Compose", "Firebase")
    )
    isLoading = false

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            job == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Job not found.")
                }
            }
            else -> {
                JobDetailsContent(job = job!!,
                    jobId = jobId,
                    onApplyClick = { jobId ->
                        val intent = Intent(context, ApplyJobActivity::class.java)
                        intent.putExtra("jobId", jobId)  // Pass jobId to ApplyJobActivity
                        context.startActivity(intent)
                    }
                    )
            }
        }
    }
}

@Composable
fun JobDetailsContent(job: JobDetails, jobId: String, onApplyClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(job.title, style = MaterialTheme.typography.headlineSmall)
        Text("Company: ${job.company}", style = MaterialTheme.typography.bodyLarge)
        Text("Location: ${job.location}", style = MaterialTheme.typography.bodyLarge)
        Text("Salary: ${job.salary}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Job Description", style = MaterialTheme.typography.titleMedium)
        Text(job.description)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Skills Required", style = MaterialTheme.typography.titleMedium)
        if (job.skills_required.isEmpty()) {
            Text("No skills mentioned.")
        } else {
            for (skill in job.skills_required) {
                Text("• $skill")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Apply Now Button
        Button(
            onClick = { onApplyClick(jobId) }, // Pass jobId when clicking
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Now")
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewJobDetailsScreen() {
////    YourAppTheme {
////
////    }
//    JobDetailsContent(
//        job = JobDetails(
//            title = "Android Developer",
//            company = "Google",
//            salary = "$120k",
//            location = "California",
//            description = "Develop cutting-edge Android applications.",
//            skills_required = listOf("Kotlin", "Jetpack Compose", "Firebase")
//        )
//    )
//}