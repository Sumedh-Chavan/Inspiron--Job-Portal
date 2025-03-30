//package com.example.csiapp_2.seeker
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.google.firebase.firestore.FirebaseFirestore
//
//// Data class representing a job posting
//data class Job(
//    val id: String = "",
//    val title: String = "",
//    val company: String = "",
//    val location: String = "",
//    val salary: String = "" // Stored as string in Firestore
//)
//
//class JobListActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            JobListScreen(
//                onJobClick = { jobId ->
//                    val intent = Intent(this, SeekerJobDetailsActivity::class.java)
//                    intent.putExtra("jobId", jobId)
//                    startActivity(intent)
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun JobListScreen(onJobClick: (String) -> Unit) {
//    var jobList by remember { mutableStateOf<List<Job>>(emptyList()) }
//    var filteredJobs by remember { mutableStateOf<List<Job>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(false) }
//    var locationFilter by remember { mutableStateOf("") }
//    var salaryFilter by remember { mutableStateOf("") }
//    var titleFilter by remember { mutableStateOf("") }
//
//    var locations by remember { mutableStateOf<List<String>>(emptyList()) }
//    var salaries by remember { mutableStateOf<List<String>>(emptyList()) }
//    var titles by remember { mutableStateOf<List<String>>(emptyList()) }
//
//    val db = FirebaseFirestore.getInstance().collection("job_posts")
//
//    // Fetch all jobs initially
//    LaunchedEffect(Unit) {
//        isLoading = true
//        db.get().addOnSuccessListener { result ->
//            val allJobs = result.documents.mapNotNull { it.toObject(Job::class.java)?.copy(id = it.id) }
//            jobList = allJobs
//            filteredJobs = allJobs // Initially, show all jobs
//            locations = allJobs.map { it.location }.distinct()
//            salaries = allJobs.map { it.salary }.distinct()
//            titles = allJobs.map { it.title }.distinct()
//            isLoading = false
//        }.addOnFailureListener {
//            isLoading = false
//        }
//    }
//
//    fun applyFilters() {
//        filteredJobs = jobList.filter { job ->
//            val locationMatches = locationFilter.isBlank() || job.location == locationFilter
//            val titleMatches = titleFilter.isBlank() || job.title == titleFilter
//            val enteredSalary = salaryFilter.toIntOrNull()
//            val jobSalary = job.salary.filter { it.isDigit() }.toIntOrNull()
//            val salaryMatches = enteredSalary == null || (jobSalary != null && jobSalary >= enteredSalary)
//            locationMatches && titleMatches && salaryMatches
//        }
//    }
//
//    Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Box(modifier = Modifier.fillMaxSize().background(
//            Color(0xFFF3E5F5) // Very light purple background
//        )) {
//            Column {
//                // Location Filter Dropdown
//                DropdownMenuFilter("Select Location", locationFilter, locations) { locationFilter = it }
//
//                // Role Filter Dropdown
//                DropdownMenuFilter("Select Role", titleFilter, titles) { titleFilter = it }
//
//                // Salary Filter Input
//                OutlinedTextField(
//                    value = salaryFilter,
//                    onValueChange = { salaryFilter = it },
//                    label = { Text("Enter Minimum Salary (Optional)") },
//                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
//                )
//
//                // Search Button
//                Button(
//                    onClick = { applyFilters() },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Search")
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                if (isLoading) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                } else {
//                    if (filteredJobs.isEmpty()) {
//                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                            Text("No jobs available.")
//                        }
//                    } else {
//                        LazyColumn(modifier = Modifier.fillMaxSize()) {
//                            items(filteredJobs) { job ->
//                                JobItem(job = job, onClick = { onJobClick(job.id) })
//                                Spacer(modifier = Modifier.height(12.dp))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DropdownMenuFilter(label: String, selectedValue: String, options: List<String>, onSelect: (String) -> Unit) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
//        Text(label, style = MaterialTheme.typography.bodyMedium)
//        Box {
//            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
//                Text(if (selectedValue.isBlank()) "Select" else selectedValue)
//            }
//            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                options.forEach { option ->
//                    DropdownMenuItem(
//                        text = { Text(option) },
//                        onClick = {
//                            onSelect(option)
//                            expanded = false
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//
//
////@Composable
////fun DropdownMenuFilter(label: String, selectedValue: String, options: List<String>, onSelect: (String) -> Unit) {
////    var expanded by remember { mutableStateOf(false) }
////
////    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
////        Text(label, style = MaterialTheme.typography.bodyMedium)
////        Box {
////            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
////                Text(if (selectedValue.isBlank()) "Select" else selectedValue)
////            }
////            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
////                options.forEach { option ->
////                    DropdownMenuItem(
////                        text = { Text(option) },
////                        onClick = {
////                            onSelect(option)
////                            expanded = false
////                        }
////                    )
////                }
////            }
////        }
////    }
////}
//
//@Composable
//fun JobItem(job: Job, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(Color(0xFF6A1B9A), Color(0xFFF1F1F1)) // Light Purple Gradient
//                )
//            ),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = job.title, style = MaterialTheme.typography.titleMedium)
//            Text(text = "Company: ${job.company}")
//            Text(text = "Location: ${job.location}")
//            Text(text = "Salary: ${job.salary}")
//        }
//    }
//}

package com.example.csiapp_2.recruiter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ✅ Define Theme Colors */
private val PurplePrimary = Color(0xFF6A1B9A)
private val White = Color.White

/** ✅ Data Model */
data class JobListing(
    val jobId: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = ""
)

/** ✅ ViewModel for Fetching Jobs */
class JobListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _jobListings = MutableStateFlow<List<JobListing>>(emptyList())
    val jobListings: StateFlow<List<JobListing>> = _jobListings

    fun fetchJobs() {
        val recruiterEmail = auth.currentUser?.email ?: return

        viewModelScope.launch {
            firestore.collection("job_posts")
                .whereEqualTo("recruiter_email", recruiterEmail)
                .get()
                .addOnSuccessListener { documents ->
                    val jobs = documents.map { doc ->
                        JobListing(
                            jobId = doc.id,
                            title = doc.getString("title") ?: "Unknown",
                            company = doc.getString("company") ?: "Unknown",
                            location = doc.getString("location") ?: "Unknown"
                        )
                    }
                    _jobListings.value = jobs
                }
                .addOnFailureListener { _jobListings.value = emptyList() }
        }
    }
}

/** ✅ Activity for Viewing Job Listings */
class JobListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JobListScreen() }
    }
}

/** ✅ UI for Job Listings */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(viewModel: JobListViewModel = viewModel()) {
    val jobListings by viewModel.jobListings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchJobs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Listings", color = White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = PurplePrimary)
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PurplePrimary, White)))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                jobListings.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No jobs available.", color = White)
                }
                else -> DisplayJobList(jobListings)
            }
        }
    }
}

/** ✅ Composable for Displaying Job Listings in Styled Cards */
@Composable
fun DisplayJobList(jobListings: List<JobListing>) {
    val context = LocalContext.current

    LazyColumn {
        items(jobListings) { job ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(context, JobDetailsActivity::class.java)
                        intent.putExtra("jobId", job.jobId)
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = job.title, fontWeight = FontWeight.Bold, color = PurplePrimary)
                    Text(text = job.company, color = Color.Gray)
                    Text(text = job.location, color = Color.Gray)
                }
            }
        }
    }
}
