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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

// Data class representing a job posting
data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val salary: String = "" // Stored as string in Firestore
)

class JobListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobListScreen(
                onJobClick = { jobId ->
                    val intent = Intent(this, SeekerJobDetailsActivity::class.java)
                    intent.putExtra("jobId", jobId)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(onJobClick: (String) -> Unit) {
    var jobList by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var locationFilter by remember { mutableStateOf("") }
    var salaryFilter by remember { mutableStateOf("") }
    var titleFilter by remember { mutableStateOf("") }

    fun fetchFilteredJobs() {
        isLoading = true
        val db = FirebaseFirestore.getInstance().collection("job_posts")

        db.get().addOnSuccessListener { result ->
            val jobs = result.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(id = doc.id)
            }.filter { job ->
                // Apply filters only if they are provided
                val locationMatches = locationFilter.isBlank() || job.location.contains(locationFilter, ignoreCase = true)
                val titleMatches = titleFilter.isBlank() || job.title.contains(titleFilter, ignoreCase = true)

                val enteredSalary = salaryFilter.toIntOrNull()
                val jobSalary = job.salary.filter { it.isDigit() }.toIntOrNull()

                val salaryMatches = enteredSalary == null || (jobSalary != null && jobSalary >= enteredSalary)

                // Job must satisfy all active filters
                locationMatches && titleMatches && salaryMatches
            }
            jobList = jobs
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            // Location Filter Input
            OutlinedTextField(
                value = locationFilter,
                onValueChange = { locationFilter = it },
                label = { Text("Enter Location (Optional)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Role Filter Input
            OutlinedTextField(
                value = titleFilter,
                onValueChange = { titleFilter = it },
                label = { Text("Enter Role (Optional)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Salary Filter Input
            OutlinedTextField(
                value = salaryFilter,
                onValueChange = { salaryFilter = it },
                label = { Text("Enter Minimum Salary (Optional)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Search Button
            Button(
                onClick = { fetchFilteredJobs() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (jobList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No jobs available.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(jobList) { job ->
                            JobItem(job = job, onClick = { onJobClick(job.id) })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobItem(job: Job, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = job.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Company: ${job.company}")
            Text(text = "Location: ${job.location}")
            Text(text = "Salary: ${job.salary}")
        }
    }
}