package com.example.csiapp_2.seeker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*

class ApplyJobActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jobId = intent.getStringExtra("jobId") ?: ""

        setContent {
            ApplyJobScreen(jobId = jobId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyJobScreen(jobId: String) {
    var resumeUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccess by remember { mutableStateOf<Boolean?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasApplied by remember { mutableStateOf(false) }

    val seekerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val userEmail = FirebaseAuth.getInstance().currentUser?.email
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val db = FirebaseFirestore.getInstance()

    // Check if the seeker has already applied
    LaunchedEffect(jobId, seekerId) {
        db.collection("job_posts").document(jobId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val applicants = document.get("applicants") as? List<String> ?: emptyList()
                    hasApplied = applicants.contains(userEmail)
                }
            }
            .addOnFailureListener {
                errorMessage = "Failed to check application status"
            }
    }

    val pickResumeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && uri.toString().endsWith(".pdf")) {
            resumeUri = uri
        } else {
            errorMessage = "Please select a valid PDF file."
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Apply for Job", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = { pickResumeLauncher.launch("application/pdf") }, enabled = !hasApplied) {
                    Text(if (resumeUri != null) "Resume Selected" else "Upload Resume (PDF)")
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isUploading) {
                    CircularProgressIndicator()
                }

                Button(
                    onClick = {
                        if (resumeUri != null) {
                            coroutineScope.launch {
                                uploadResumeAndApply(
                                    seekerId = seekerId,
                                    jobId = jobId,
                                    resumeUri = resumeUri!!,
                                    onResult = { success, error ->
                                        uploadSuccess = success
                                        if (error != null) {
                                            errorMessage = error
                                        }
                                        if (success) {
                                            hasApplied = true
                                        }
                                    },
                                    setUploading = { isUploading = it }
                                )
                            }
                        }
                    },
                    enabled = !isUploading && resumeUri != null && !hasApplied,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasApplied) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (hasApplied) "Applied" else "Apply Now")
                }

                Spacer(modifier = Modifier.height(20.dp))

                uploadSuccess?.let { success ->
                    Text(
                        if (success) "Application Submitted Successfully!" else "Application Failed. Try Again.",
                        color = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }

                errorMessage?.let { message ->
                    LaunchedEffect(message) {
                        snackbarHostState.showSnackbar(message)
                        errorMessage = null
                    }
                }
            }
        }
    }
}


fun uploadResumeAndApply(
    seekerId: String,
    jobId: String,
    resumeUri: Uri,
    onResult: (Boolean, String?) -> Unit,
    setUploading: (Boolean) -> Unit
) {
    setUploading(true)

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userEmail = auth.currentUser?.email

    if (userEmail == null) {
        onResult(false, "User email not found.")
        setUploading(false)
        return
    }

    val storageRef = FirebaseStorage.getInstance()
        .getReference("resumes/$seekerId/$jobId.pdf")

    storageRef.putFile(resumeUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { resumeDownloadUri ->

                val application = hashMapOf(
                    "seeker_id" to seekerId,
                    "job_id" to jobId,
                    "status" to "pending",
                    "resumeUrl" to resumeDownloadUri.toString()
                )

                db.collection("applications")
                    .add(application)
                    .addOnSuccessListener {
                        // Now update the job post with the applicant's email
                        val jobPostRef = db.collection("job_posts").document(jobId)

                        jobPostRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val currentApplicants = document.get("applicants") as? MutableList<String> ?: mutableListOf()
                                    if (!currentApplicants.contains(userEmail)) {
                                        currentApplicants.add(userEmail)

                                        jobPostRef.update("applicants", currentApplicants)
                                            .addOnSuccessListener {
                                                onResult(true, null)
                                            }
                                            .addOnFailureListener { e ->
                                                onResult(false, "Failed to update job post: ${e.message}")
                                            }
                                    } else {
                                        onResult(true, null) // Already applied
                                    }
                                } else {
                                    onResult(false, "Job post not found.")
                                }
                                setUploading(false)
                            }
                            .addOnFailureListener { e ->
                                onResult(false, "Failed to fetch job post: ${e.message}")
                                setUploading(false)
                            }
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed to submit application: ${e.message}")
                        setUploading(false)
                    }

            }.addOnFailureListener { e ->
                onResult(false, "Failed to get resume URL: ${e.message}")
                setUploading(false)
            }
        }
        .addOnFailureListener { e ->
            onResult(false, "Resume upload failed: ${e.message}")
            setUploading(false)
        }
}
