package com.example.csiapp_2.recruiter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

data class Applicant(
    val name: String = "",
    val experience: String = "",
    val email: String = ""
)

class JobDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jobId = intent.getStringExtra("jobId") ?: ""
        if (jobId.isBlank()) {
            Log.e("JobDetailsActivity", "Error: Received blank jobId!")
        } else {
            Log.d("JobDetailsActivity", "Received jobId: $jobId")
        }
        setContent {
            ApplicantsListScreen(jobId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantsListScreen(jobId: String) {
    val firestore = FirebaseFirestore.getInstance()
    var names by remember { mutableStateOf<List<String>>(emptyList()) }
    var experiences by remember { mutableStateOf<List<String>>(emptyList()) }
    var emails by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(jobId) {
        fetchApplicantsForJob(firestore, jobId) { fetchedNames, fetchedExperiences, fetchedEmails ->
            names = fetchedNames
            experiences = fetchedExperiences
            emails = fetchedEmails
            isLoading = false
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Applicants") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                names.isEmpty() -> Text("No applicants for this job.", modifier = Modifier.padding(8.dp))
                else -> DisplayApplicantsTable(names, experiences, emails)
            }
        }
    }
}

/** ✅ Display applicants in a structured tabular format */
@Composable
fun DisplayApplicantsTable(names: List<String>, experiences: List<String>, emails: List<String>) {
    LazyColumn {
        items(names.indices.toList()) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: ${names[index]}")
                    Text(text = "Email: ${emails[index]}")
                    Text(text = "Experience: ${experiences[index]}")
                }
            }
        }
    }
}

/** ✅ Fetch applicants using Job ID */
fun fetchApplicantsForJob(
    firestore: FirebaseFirestore,
    jobId: String,
    onApplicantsFetched: (List<String>, List<String>, List<String>) -> Unit
) {
    if (jobId.isBlank()) {
        Log.e("Firestore", "Error: jobId is blank!")
        onApplicantsFetched(emptyList(), emptyList(), emptyList())
        return
    }

    firestore.collection("job_posts").document(jobId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val applicantsEmails = document.get("applicants") as? List<String> ?: emptyList()
                Log.d("Firestore", "Applicants Emails: $applicantsEmails")

                if (applicantsEmails.isNotEmpty()) {
                    fetchApplicantDetails(applicantsEmails, onApplicantsFetched)
                } else {
                    Log.e("Firestore", "No applicants found for this job.")
                    onApplicantsFetched(emptyList(), emptyList(), emptyList())
                }
            } else {
                Log.e("Firestore", "No job found with jobId: $jobId")
                onApplicantsFetched(emptyList(), emptyList(), emptyList())
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Error fetching job by jobId", exception)
            onApplicantsFetched(emptyList(), emptyList(), emptyList())
        }
}

/** ✅ Fetch applicant details from `users` collection */
private fun fetchApplicantDetails(
    emails: List<String>,
    onApplicantsFetched: (List<String>, List<String>, List<String>) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val namesList = mutableListOf<String>()
    val experienceList = mutableListOf<String>()
    val emailsList = mutableListOf<String>()

    var fetchedCount = 0 // Track the number of processed emails

    for (email in emails) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name") ?: "N/A"
                    val experience = document.getString("experience") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"

                    namesList.add(name)
                    experienceList.add(experience)
                    emailsList.add(email)
                }
                fetchedCount++

                // Ensure callback is triggered only when all queries complete
                if (fetchedCount == emails.size) {
                    onApplicantsFetched(namesList, experienceList, emailsList)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching applicant details", e)
                fetchedCount++

                if (fetchedCount == emails.size) {
                    onApplicantsFetched(namesList, experienceList, emailsList)
                }
            }
    }
}
