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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val salary: String = ""
)

class JobListActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobListScreen(
                onJobClick = {
                        jobId ->
                    val intent = Intent(this, JobDetailsActivity::class.java)
                    intent.putExtra("jobId", jobId)
                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun JobListScreen(
    onJobClick: (String) -> Unit
) {
    var jobList by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

//    // Fetch jobs from Firestore
//    LaunchedEffect(Unit) {
//        FirebaseFirestore.getInstance()
//            .collection("job_posts")
//            .get()
//            .addOnSuccessListener { result ->
//                val jobs = result.documents.mapNotNull { doc ->
//                    doc.toObject(Job::class.java)?.copy(id = doc.id)
//                }
//                jobList = jobs
//                isLoading = false
//            }
//            .addOnFailureListener {
//                isLoading = false
//            }
//    }

    //mock data
    jobList = listOf(
        Job("1", "Software Engineer", "Google", "California", "$120k"),
        Job("2", "Data Analyst", "Amazon", "New York", "$100k")
    )
    isLoading = false


    Surface(modifier = Modifier.fillMaxSize()) {
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
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


@Preview(showBackground = true)
@Composable
fun PreviewJobListScreen() {
    JobListScreen(
        onJobClick = {}
    )
}