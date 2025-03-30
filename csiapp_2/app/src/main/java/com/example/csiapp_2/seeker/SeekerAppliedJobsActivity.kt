package com.example.csiapp_2.seeker

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppliedJobsScreen() {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    var userEmail by remember { mutableStateOf<String?>(null) }
    var appliedJobs by remember { mutableStateOf<List<AppliedJob>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Applications", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6A1B9A))
            )
        },
        bottomBar = { SeekerBottomNavigationBar(currentScreen = "applications") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF7B1FA2), Color.White)))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            } else if (appliedJobs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No applications found.", color = Color.White)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(appliedJobs) { job ->
                        AppliedJobItem(job = job)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AppliedJobItem(job: AppliedJob) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                val intent = Intent(context, SeekerViewJobDetailsActivity::class.java)
                intent.putExtra("jobId", job.id)
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = job.title, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Company: ${job.company}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Status: ${job.status}", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SeekerBottomNavigationBar(currentScreen: String) {
    val context = LocalContext.current
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == "home",
            onClick = {
                context.startActivity(Intent(context, SeekerDashboardActivity::class.java))
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Inbox, contentDescription = "Inbox") },
            label = { Text("Inbox") },
            selected = currentScreen == "inbox",
            onClick = {
                context.startActivity(Intent(context, SeekerInboxActivity::class.java))
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentScreen == "profile",
            onClick = {
                context.startActivity(Intent(context, SeekerProfileActivity::class.java))
            }
        )
    }
}