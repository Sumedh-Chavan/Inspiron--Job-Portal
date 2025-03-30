package com.example.csiapp_2.recruiter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostJobActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostJobScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen() {
    var companyname by remember { mutableStateOf(TextFieldValue()) }
    var jobTitle by remember { mutableStateOf(TextFieldValue()) }
    var jobDescription by remember { mutableStateOf(TextFieldValue()) }
    var salary by remember { mutableStateOf(TextFieldValue()) }
    var location by remember { mutableStateOf(TextFieldValue()) }
    var deadlineDate by remember { mutableStateOf(TextFieldValue()) }
    var skillsRequired by remember { mutableStateOf(TextFieldValue()) }

    var companynameError by remember { mutableStateOf(false) }
    var jobTitleError by remember { mutableStateOf(false) }
    var jobDescriptionError by remember { mutableStateOf(false) }
    var salaryError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }
    var deadlineDateError by remember { mutableStateOf(false) }
    var skillsRequiredError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    val isFormValid = companyname.text.isNotBlank() &&
            jobTitle.text.isNotBlank() &&
            jobDescription.text.isNotBlank() &&
            salary.text.isNotBlank() &&
            location.text.isNotBlank() &&
            deadlineDate.text.isNotBlank() &&
            skillsRequired.text.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Post a Job") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = companyname,
                onValueChange = { companyname = it; companynameError = it.text.isBlank() },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = companynameError
            )
            if (companynameError) Text("Company name is required", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it; jobTitleError = it.text.isBlank() },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = jobTitleError
            )
            if (jobTitleError) Text("Job title is required", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = jobDescription,
                onValueChange = { jobDescription = it; jobDescriptionError = it.text.isBlank() },
                label = { Text("Job Description") },
                modifier = Modifier.fillMaxWidth(),
                isError = jobDescriptionError
            )
            if (jobDescriptionError) Text("Job description is required", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it; salaryError = it.text.isBlank() },
                label = { Text("Salary") },
                modifier = Modifier.fillMaxWidth(),
                isError = salaryError
            )
            if (salaryError) Text("Salary is required", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = location,
                onValueChange = { location = it; locationError = it.text.isBlank() },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                isError = locationError
            )
            if (locationError) Text("Location is required", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = deadlineDate,
                onValueChange = { deadlineDate = it; deadlineDateError = it.text.isBlank() },
                label = { Text("Deadline Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                isError = deadlineDateError
            )
            if (deadlineDateError) Text("Deadline date is required", color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = skillsRequired,
                onValueChange = { skillsRequired = it; skillsRequiredError = it.text.isBlank() },
                label = { Text("Skills Required (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
                isError = skillsRequiredError
            )
            if (skillsRequiredError) Text("Skills are required", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!isFormValid) {
                        companynameError = companyname.text.isBlank()
                        jobTitleError = jobTitle.text.isBlank()
                        jobDescriptionError = jobDescription.text.isBlank()
                        salaryError = salary.text.isBlank()
                        locationError = location.text.isBlank()
                        deadlineDateError = deadlineDate.text.isBlank()
                        skillsRequiredError = skillsRequired.text.isBlank()
                        return@Button
                    }

                    val job = hashMapOf(
                        "title" to jobTitle.text.trim(),
                        "company" to companyname.text.trim(),
                        "description" to jobDescription.text.trim(),
                        "salary" to salary.text.trim(),
                        "location" to location.text.trim(),
                        "posted_on" to Timestamp(Date()),
                        "skills_required" to skillsRequired.text.split(",").map { it.trim() },
                        "applicants" to 0,
                        "selected_seekers" to emptyList<String>()
                    )

                    firestore.collection("job_posts")
                        .add(job)
                        .addOnSuccessListener { jobDocRef ->
                            firestore.collection("users").whereEqualTo("role", "seeker")
                                .get()
                                .addOnSuccessListener { seekers ->
                                    if (seekers.isEmpty) {
                                        Toast.makeText(context, "No seekers found!", Toast.LENGTH_SHORT).show()
                                        return@addOnSuccessListener
                                    }

                                    for (seeker in seekers.documents) {
                                        val seekerRef = firestore.collection("users").document(seeker.id)
                                        val notification = mapOf(
                                            "company" to companyname.text.trim(),
                                            "role" to jobTitle.text.trim(),
                                            "salary" to salary.text.trim()
                                        )

                                        seekerRef.update("notifications", FieldValue.arrayUnion(notification))


                                        seekerRef.update("notifications", FieldValue.arrayUnion(notification))
                                            .addOnSuccessListener {
                                                println("✅ Notification added for ${seeker.id}")
                                            }
                                            .addOnFailureListener { e ->
                                                println("❌ Failed to update notifications for ${seeker.id}: ${e.message}")
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    println("❌ Error fetching seekers: ${e.message}")
                                }

                            Toast.makeText(context, "Job Posted Successfully!", Toast.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(context, RecruiterDashboardActivity::class.java)
                                context.startActivity(intent)
                            }, 2000)
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Post Job")
            }
        }
    }
}