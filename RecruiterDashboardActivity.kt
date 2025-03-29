//package com.example.csiapp_2.recruiter
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.navigation.compose.rememberNavController
//import androidx.compose.ui.tooling.preview.Preview
//import com.google.firebase.auth.FirebaseAuth
//
//class RecruiterDashboardActivity : ComponentActivity() {
//    private lateinit var auth : FirebaseAuth
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth  = FirebaseAuth.getInstance()
//        setContent {
//            RecruiterDashboardScreen()
//        }
//    }
//}
//
//@Preview
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RecruiterDashboardScreen() {
//    val navController = rememberNavController()
//    val context = LocalContext.current
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Welcome Recruiter") }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(24.dp)
//        ) {
//            Button(
//                onClick = { val intent = Intent(context , PostJobActivity::class.java)
//                    context.startActivity(intent)},
//                modifier = Modifier.fillMaxWidth().padding(8.dp)
//            ) {
//                Text("Post a Job")
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Button(
//                onClick = { val intent = Intent(context , ViewApplicantsActivity::class.java)
//                    context.startActivity(intent) },
//                modifier = Modifier.fillMaxWidth().padding(8.dp)
//            ) {
//                Text("View Applicants")
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Button(
//                onClick = { val intent = Intent(context , RecruiterInboxActivity::class.java)
//                    context.startActivity(intent) },
//                modifier = Modifier.fillMaxWidth().padding(8.dp)
//            ) {
//                Text("Inbox")
//            }
//        }
//    }
//}

package com.example.csiapp_2.recruiter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ✅ ViewModel to fetch job statistics */
class RecruiterDashboardViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val recruiterEmail = auth.currentUser?.email ?: ""

    private val _jobCount = MutableStateFlow(0)
    val jobCount: StateFlow<Int> = _jobCount

    private val _totalApplicants = MutableStateFlow(0)
    val totalApplicants: StateFlow<Int> = _totalApplicants

    init {
        fetchRecruiterStats()
    }

    /** ✅ Fetch statistics for jobs posted and total applicants */
    private fun fetchRecruiterStats() {
        if (recruiterEmail.isBlank()) {
            Log.e("RecruiterDashboardVM", "Recruiter email is blank!")
            return
        }

        firestore.collection("job_posts")
            .whereEqualTo("email", recruiterEmail)
            .get()
            .addOnSuccessListener { documents ->
                val jobCount = documents.size()
                var totalApplicants = 0

                for (document in documents) {
                    val applicants = document.get("applicants") as? List<String> ?: emptyList()
                    totalApplicants += applicants.size
                }

                _jobCount.value = jobCount
                _totalApplicants.value = totalApplicants
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching job statistics", e)
            }
    }
}

/** ✅ Main Activity */
class RecruiterDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecruiterDashboardScreen()
        }
    }
}

/** ✅ Composable Dashboard Screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterDashboardScreen(viewModel: RecruiterDashboardViewModel = viewModel()) {
    val context = LocalContext.current
    val jobCount by viewModel.jobCount.collectAsState()
    val totalApplicants by viewModel.totalApplicants.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recruiter Dashboard") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Display job statistics
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Jobs Posted: $jobCount", style = MaterialTheme.typography.titleMedium)
                    Text("Total Applicants: $totalApplicants", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons for navigation
            Button(
                onClick = { context.startActivity(Intent(context, PostJobActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Post a Job")
            }

            Button(
                onClick = { context.startActivity(Intent(context, ViewApplicantsActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("View Applicants")
            }

            Button(
                onClick = { context.startActivity(Intent(context, RecruiterInboxActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Inbox")
            }
        }
    }
}
