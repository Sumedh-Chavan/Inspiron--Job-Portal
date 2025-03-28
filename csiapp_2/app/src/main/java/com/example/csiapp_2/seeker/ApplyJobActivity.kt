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

    val seekerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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

                Button(onClick = { pickResumeLauncher.launch("application/pdf") }) {
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
                                    },
                                    setUploading = { isUploading = it }
                                )
                            }
                        }
                    },
                    enabled = !isUploading && resumeUri != null
                ) {
                    Text(if (isUploading) "Uploading..." else "Apply Now")
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

    // real firebase stepup
//    val storageRef = FirebaseStorage.getInstance()
//        .getReference("resumes/$seekerId/$jobId.pdf")
//
//    storageRef.putFile(resumeUri)
//        .addOnSuccessListener {
//            storageRef.downloadUrl.addOnSuccessListener { resumeDownloadUri ->
//
//                val application = hashMapOf(
//                    "seeker_id" to seekerId,
//                    "job_id" to jobId,
//                    "status" to "pending",
//                    "resumeUrl" to resumeDownloadUri.toString()
//                )
//
//                FirebaseFirestore.getInstance()
//                    .collection("applications")
//                    .add(application)
//                    .addOnSuccessListener {
//                        onResult(true, null)
//                        setUploading(false)
//                    }
//                    .addOnFailureListener { e ->
//                        onResult(false, "Failed to submit application: ${e.message}")
//                        setUploading(false)
//                    }
//
//            }.addOnFailureListener { e ->
//                onResult(false, "Failed to get resume URL: ${e.message}")
//                setUploading(false)
//            }
//        }
//        .addOnFailureListener { e ->
//            onResult(false, "Resume upload failed: ${e.message}")
//            setUploading(false)
//        }

    // mockup
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        if (jobId.isNotEmpty()) {
            onResult(true, null) // Mock success
        } else {
            onResult(false, "Mocked: Failed to submit application.") // Mock failure
        }
        setUploading(false)
    }, 2000) // Simulate network delay
}