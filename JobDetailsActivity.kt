package com.example.csiapp_2.recruiter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Applicant(
    val name: String = "",
    val experience: String = "",
    val email: String = "",
    val resumeUrl: String = ""
)

class ApplicantsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _applicants = MutableStateFlow<List<Applicant>>(emptyList())
    val applicants: StateFlow<List<Applicant>> = _applicants

    fun fetchApplicants(jobId: String) {
        val recruiterEmail = auth.currentUser?.email ?: return
        Log.d("ApplicantsViewModel", "Recruiter Email: $recruiterEmail")

        viewModelScope.launch {
            firestore.collection("job_posts").document(jobId).get()
                .addOnSuccessListener { document ->
                    val emails = document.get("applicants") as? List<String> ?: emptyList()
                    Log.d("Firestore", "Applicants Emails: $emails")

                    fetchApplicantDetails(recruiterEmail, emails) { fetchedApplicants ->
                        _applicants.value = fetchedApplicants
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching job applicants", e)
                    _applicants.value = emptyList()
                }
        }
    }

    private fun fetchApplicantDetails(recruiterEmail: String, emails: List<String>, onFetched: (List<Applicant>) -> Unit) {
        val applicantsList = mutableListOf<Applicant>()
        var fetchedCount = 0

        for (email in emails) {
            firestore.collection("applications")
                .whereEqualTo("recruiter_email", recruiterEmail)
                .whereEqualTo("seeker_email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.getString("name") ?: "N/A"
                        val experience = document.getString("experience") ?: "N/A"
                        val resumeUrl = document.getString("resumeUrl") ?: "N/A"

                        applicantsList.add(Applicant(name, experience, email, resumeUrl))
                    }
                    fetchedCount++
                    if (fetchedCount == emails.size) {
                        onFetched(applicantsList)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching applicant details", e)
                    fetchedCount++
                    if (fetchedCount == emails.size) {
                        onFetched(applicantsList)
                    }
                }
        }
    }
}

class JobDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jobId = intent.getStringExtra("jobId") ?: ""
        setContent { ApplicantsListScreen(jobId) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantsListScreen(jobId: String, viewModel: ApplicantsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val applicants by viewModel.applicants.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.fetchApplicants(jobId)
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
                applicants.isEmpty() -> Text("No applicants found.", modifier = Modifier.padding(8.dp))
                else -> DisplayApplicantsTable(applicants)
            }
        }
    }
}

@Composable
fun DisplayApplicantsTable(applicants: List<Applicant>) {
    val context = LocalContext.current

    LazyColumn {
        items(applicants) { applicant ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${applicant.name}")
                    Text("Email: ${applicant.email}")
                    Text("Experience: ${applicant.experience}")
                    Text(
                        text = "Resume: ${applicant.resumeUrl}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(applicant.resumeUrl))
                            context.startActivity(intent)
                        }
                    )
                    Log.d("FirestoreDebug", "Fetched Resume Url: ${applicant.resumeUrl}")
                }
            }
        }
    }
}
