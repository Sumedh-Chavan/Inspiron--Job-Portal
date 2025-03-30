//package com.example.csiapp_2.recruiter
//
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//data class Applicant(
//    val name: String = "",
//    val experience: String = "",
//    val email: String = "",
//    val resumeUrl: String = ""
//)
//
//class ApplicantsViewModel : ViewModel() {
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//    private val _applicants = MutableStateFlow<List<Applicant>>(emptyList())
//    val applicants: StateFlow<List<Applicant>> = _applicants
//
//    fun fetchApplicants(jobId: String) {
//        val recruiterEmail = auth.currentUser?.email ?: return
//        Log.d("ApplicantsViewModel", "Recruiter Email: $recruiterEmail")
//
//        viewModelScope.launch {
//            firestore.collection("job_posts").document(jobId).get()
//                .addOnSuccessListener { document ->
//                    val emails = document.get("applicants") as? List<String> ?: emptyList()
//                    Log.d("Firestore", "Applicants Emails: $emails")
//
//                    fetchApplicantDetails(recruiterEmail, emails) { fetchedApplicants ->
//                        _applicants.value = fetchedApplicants
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("Firestore", "Error fetching job applicants", e)
//                    _applicants.value = emptyList()
//                }
//        }
//    }
//
//    private fun fetchApplicantDetails(recruiterEmail: String, emails: List<String>, onFetched: (List<Applicant>) -> Unit) {
//        val applicantsList = mutableListOf<Applicant>()
//        var fetchedCount = 0
//
//        for (email in emails) {
//            firestore.collection("applications")
//                .whereEqualTo("recruiter_email", recruiterEmail)
//                .whereEqualTo("seeker_email", email)
//                .get()
//                .addOnSuccessListener { documents ->
//                    for (document in documents) {
//                        val name = document.getString("name") ?: "N/A"
//                        val experience = document.getString("experience") ?: "N/A"
//                        val resumeUrl = document.getString("resumeUrl") ?: "N/A"
//
//                        applicantsList.add(Applicant(name, experience, email, resumeUrl))
//                    }
//                    fetchedCount++
//                    if (fetchedCount == emails.size) {
//                        onFetched(applicantsList)
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("Firestore", "Error fetching applicant details", e)
//                    fetchedCount++
//                    if (fetchedCount == emails.size) {
//                        onFetched(applicantsList)
//                    }
//                }
//        }
//    }
//}
//
//class JobDetailsActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val jobId = intent.getStringExtra("jobId") ?: ""
//        setContent { ApplicantsListScreen(jobId) }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ApplicantsListScreen(jobId: String, viewModel: ApplicantsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
//    val applicants by viewModel.applicants.collectAsState()
//
//    LaunchedEffect(jobId) {
//        viewModel.fetchApplicants(jobId)
//    }
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Applicants") }) }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//            when {
//                applicants.isEmpty() -> Text("No applicants found.", modifier = Modifier.padding(8.dp))
//                else -> DisplayApplicantsTable(applicants)
//            }
//        }
//    }
//}
//
//@Composable
//fun DisplayApplicantsTable(applicants: List<Applicant>) {
//    val context = LocalContext.current
//
//    LazyColumn {
//        items(applicants) { applicant ->
//            Card(
//                modifier = Modifier.fillMaxWidth().padding(8.dp),
//                elevation = CardDefaults.cardElevation(2.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text("Name: ${applicant.name}")
//                    Text("Email: ${applicant.email}")
//                    Text("Experience: ${applicant.experience}")
//                    Text(
//                        text = "Resume: ${applicant.resumeUrl}",
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.clickable {
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(applicant.resumeUrl))
//                            context.startActivity(intent)
//                        }
//                    )
//                    Log.d("FirestoreDebug", "Fetched Resume Url: ${applicant.resumeUrl}")
//                }
//            }
//        }
//    }
//}

package com.example.csiapp_2.recruiter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ✅ Define Theme Colors */
private val PurplePrimary = Color(0xFF6A1B9A)
private val White = Color.White

/** ✅ Data Model */
data class Applicant(
    val name: String = "",
    val experience: String = "",
    val email: String = "",
    val resumeUrl: String = ""
)

/** ✅ ViewModel for Fetching Applicants */
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

                    fetchApplicantDetails(emails) { fetchedApplicants ->
                        _applicants.value = fetchedApplicants
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching job applicants", e)
                    _applicants.value = emptyList()
                }
        }
    }

    private fun fetchApplicantDetails(emails: List<String>, onFetched: (List<Applicant>) -> Unit) {
        val applicantsList = mutableListOf<Applicant>()
        var fetchedCount = 0

        if (emails.isEmpty()) {
            onFetched(emptyList())
            return
        }

        for (email in emails) {
            Log.d("FirestoreDebug", "Fetching details for: $email")

            // Query "users" collection where "email" field matches
            firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)  // We only need one document
                .get()
                .addOnSuccessListener { userDocs ->
                    if (userDocs.isEmpty) {
                        Log.e("Firestore", "User not found for email: $email")
                        fetchedCount++
                        checkFetchCompletion(fetchedCount, emails.size, applicantsList, onFetched)
                        return@addOnSuccessListener
                    }

                    val userDoc = userDocs.documents.first()
                    val name = userDoc.getString("name") ?: "Unknown"
                    val experience = userDoc.getString("experience") ?: "No experience provided"

                    // Query "applications" collection where "seeker_email" matches
                    firestore.collection("applications")
                        .whereEqualTo("seeker_email", email)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { appDocs ->
                            val resumeUrl = if (!appDocs.isEmpty) {
                                appDocs.documents.first().getString("resumeUrl") ?: "N/A"
                            } else {
                                "N/A"
                            }

                            applicantsList.add(Applicant(name, experience, email, resumeUrl))
                            fetchedCount++
                            checkFetchCompletion(fetchedCount, emails.size, applicantsList, onFetched)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching resume URL", e)
                            fetchedCount++
                            checkFetchCompletion(fetchedCount, emails.size, applicantsList, onFetched)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching user details", e)
                    fetchedCount++
                    checkFetchCompletion(fetchedCount, emails.size, applicantsList, onFetched)
                }
        }
    }

    /** ✅ Helper function to check when all queries are done */
    private fun checkFetchCompletion(
        fetchedCount: Int,
        totalEmails: Int,
        applicantsList: List<Applicant>,
        onFetched: (List<Applicant>) -> Unit
    ) {
        if (fetchedCount == totalEmails) {
            onFetched(applicantsList)
        }
    }

}

/** ✅ Activity for Viewing Applicants */
class JobDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jobId = intent.getStringExtra("jobId") ?: ""
        setContent { ApplicantsListScreen(jobId) }
    }
}

/** ✅ UI for Applicants List */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantsListScreen(jobId: String, viewModel: ApplicantsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val applicants by viewModel.applicants.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.fetchApplicants(jobId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicants", color = White) },
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
                applicants.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No applicants found.", color = White)
                }
                else -> DisplayApplicantsTable(applicants)
            }
        }
    }
}

/** ✅ Composable for Displaying Applicants in a Styled Table */
@Composable
fun DisplayApplicantsTable(applicants: List<Applicant>) {
    val context = LocalContext.current

    LazyColumn {
        items(applicants) { applicant ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Name: ${applicant.name}", fontWeight = FontWeight.Bold, color = PurplePrimary)
                    Text(text = "Email: ${applicant.email}", color = Color.Gray)
                    Text(text = "Experience: ${applicant.experience}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    /** ✅ Clickable Resume File Icon */
                    if (applicant.resumeUrl != "N/A") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(applicant.resumeUrl))
                                    context.startActivity(intent)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Resume",
                                tint = PurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Resume", color = PurplePrimary, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("No resume available", color = Color.Gray)
                    }
                }
            }
        }
    }
}
