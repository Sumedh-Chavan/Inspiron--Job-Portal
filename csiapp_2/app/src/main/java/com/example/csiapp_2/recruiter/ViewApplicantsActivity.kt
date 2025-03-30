//package com.example.csiapp_2.recruiter
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class ViewApplicantsActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            JobsListScreen()
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun JobsListScreen() {
//    val firestore = FirebaseFirestore.getInstance()
//    val auth = FirebaseAuth.getInstance()
//    val recruiterEmail = auth.currentUser?.email ?: ""
//
//    var jobs by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // Fetch jobs for the current recruiter
//    LaunchedEffect(recruiterEmail) {
//        fetchJobsForRecruiter(firestore, recruiterEmail) { fetchedJobs ->
//            jobs = fetchedJobs
//            isLoading = false
//        }
//    }
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Your Posted Jobs") }) }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//            } else if (jobs.isEmpty()) {
//                Text("No jobs posted.", modifier = Modifier.padding(8.dp))
//            } else {
//                LazyColumn {
//                    items(jobs) { job ->
//                        JobItem(job)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun JobItem(job: Map<String, Any>) {
//    val context = LocalContext.current
//    val jobId = job["id"] as? String ?: ""
//    val company = job["company"] as? String ?: "Unknown Company"
//    val description = job["description"] as? String ?: "No Description"
//    val location = job["location"] as? String ?: "Unknown Location"
//
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(8.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = "Company: $company", style = MaterialTheme.typography.bodyMedium)
//            Text(text = "Description: $description", style = MaterialTheme.typography.bodyMedium)
//            Text(text = "Location: $location", style = MaterialTheme.typography.bodyMedium)
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(
//                onClick = {
//                    val intent = Intent(context, JobDetailsActivity::class.java)
//                    intent.putExtra("jobId", job.id)
//                    context.startActivity(intent)
//                }
//            ) {
//                Text("View More")
//            }
//        }
//    }
//}
//
//// Function to fetch jobs for the recruiter based on email
//fun fetchJobsForRecruiter(
//    firestore: FirebaseFirestore,
//    recruiterEmail: String,
//    onJobsFetched: (List<Map<String, Any>>) -> Unit
//) {
//    firestore.collection("job_posts")
//        .whereEqualTo("email", recruiterEmail)
//        .get()
//        .addOnSuccessListener { documents ->
//            val jobs = documents.map { it.data }
//            onJobsFetched(jobs)
//        }
//        .addOnFailureListener { exception ->
//            Log.e("Firestore", "Error fetching jobs", exception)
//            onJobsFetched(emptyList())
//        }
//}

package com.example.csiapp_2.recruiter

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val salary: String = ""
)

class ViewApplicantsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewApplicantsScreen(
                onJobClick = { jobId ->
                    val intent = Intent(this, JobDetailsActivity::class.java)
                    intent.putExtra("jobId", jobId)
                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun ViewApplicantsScreen(
    onJobClick: (String) -> Unit
) {
    var jobList by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val recruiterEmail = FirebaseAuth.getInstance().currentUser?.email

    LaunchedEffect(Unit) {
        if (recruiterEmail != null) {
            FirebaseFirestore.getInstance()
                .collection("job_posts")
                .whereEqualTo("email", recruiterEmail)
                .get()
                .addOnSuccessListener { result ->
                    val jobs = result.documents.mapNotNull { doc ->
                        doc.toObject(Job::class.java)?.copy(id = doc.id)
                    }
                    jobList = jobs
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

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

//@Preview(showBackground = true)
//@Composable
//fun PreviewViewApplicantsScreen() {
//    ViewApplicantsScreen(
//        onJobClick = {}
//    )
//}
