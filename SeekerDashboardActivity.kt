package com.example.csiapp_2.seeker

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
import com.example.csiapp_2.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** ✅ ViewModel to fetch applied jobs statistics */
class SeekerDashboardViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val seekerEmail = auth.currentUser?.email ?: ""

    private val _appliedJobCount = MutableStateFlow(0)
    val appliedJobCount: StateFlow<Int> = _appliedJobCount

    init {
        fetchAppliedJobs()
    }

    /** ✅ Fetch count of applied jobs */
    private fun fetchAppliedJobs() {
        if (seekerEmail.isBlank()) {
            Log.e("SeekerDashboardVM", "Seeker email is blank!")
            return
        }

        firestore.collection("applications")
            .whereEqualTo("seekerEmail", seekerEmail)
            .get()
            .addOnSuccessListener { documents ->
                _appliedJobCount.value = documents.size()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching applied jobs", e)
            }
    }
}

/** ✅ Seeker Dashboard Activity */
class SeekerDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeekerDashboardScreen()
        }
    }
}

/** ✅ Seeker Dashboard Screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerDashboardScreen(viewModel: SeekerDashboardViewModel = viewModel()) {
    val context = LocalContext.current
    val appliedJobCount by viewModel.appliedJobCount.collectAsState()
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seeker Dashboard") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Applied Jobs Count
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Jobs Applied: $appliedJobCount", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View Applied Jobs Button
            Button(
                onClick = { context.startActivity(Intent(context, ApplyJobActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("View Applied Jobs")
            }

            // View Job Listings Button
            Button(
                onClick = { context.startActivity(Intent(context, JobListActivity::class.java)) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Browse Jobs")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = {
                    auth.signOut()  // ✅ Logs out the user
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
    }
}
