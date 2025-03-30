package com.example.csiapp_2.recruiter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
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

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    val isFormValid = companyname.text.isNotBlank() &&
            jobTitle.text.isNotBlank() &&
            jobDescription.text.isNotBlank() &&
            salary.text.isNotBlank() &&
            location.text.isNotBlank() &&
            deadlineDate.text.isNotBlank() &&
            skillsRequired.text.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6A1B9A), Color.White)
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Post a Job",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold) // BOLD HEADING
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
//            bottomBar = { RecruiterBottomNavBar(context) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                JobInputField("Company Name", companyname) { companyname = it }
                JobInputField("Job Title", jobTitle) { jobTitle = it }
                JobInputField("Job Description", jobDescription) { jobDescription = it }
                JobInputField("Salary", salary) { salary = it }
                JobInputField("Location", location) { location = it }
                JobInputField("Deadline Date (yyyy-MM-dd)", deadlineDate) { deadlineDate = it }
                JobInputField("Skills Required (comma-separated)", skillsRequired) { skillsRequired = it }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!isFormValid) return@Button

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
                            .addOnSuccessListener {
                                Toast.makeText(context, "Job Posted Successfully!", Toast.LENGTH_SHORT).show()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(context, RecruiterDashboardActivity::class.java)
                                    context.startActivity(intent)
                                }, 2000)
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Post Job", color = Color.White)
                }
            }
        }
    }
}



@Composable
fun JobInputField(label: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Black) }, // Placeholder in Black
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(Color.White, RoundedCornerShape(8.dp)), // White Background
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White, // White Background when focused
            unfocusedContainerColor = Color.White, // White Background when not focused
            cursorColor = Color.Black, // Cursor in Black
            focusedIndicatorColor = Color.Transparent, // No Border when Focused
            unfocusedIndicatorColor = Color.Transparent, // No Border when Unfocused
            focusedTextColor = Color.Black, // Input Text in Black
            unfocusedTextColor = Color.Black // Input Text in Black
        )
    )
}


//@Composable
//fun RecruiterBottomNavBar(context: android.content.Context) {
//    NavigationBar(
//        containerColor = Color.White
//    ) {
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black) },
//            label = { Text("Home", color = Color.Black) },
//            selected = false,
//            onClick = {
//                val intent = Intent(context, RecruiterDashboardActivity::class.java)
//                context.startActivity(intent)
//            }
//        )
//
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Mail, contentDescription = "Inbox", tint = Color.Black) },
//            label = { Text("Inbox", color = Color.Black) },
//            selected = false,
//            onClick = {
//                val intent = Intent(context, RecruiterInboxActivity::class.java)
//                context.startActivity(intent)
//            }
//        )
//
//        NavigationBarItem(
//            icon = { Icon(Icons.Filled.People, contentDescription = "Applicants", tint = Color.Black) },
//            label = { Text("Applicants", color = Color.Black) },
//            selected = false,
//            onClick = {
//                val intent = Intent(context, ViewApplicantsActivity::class.java)
//                context.startActivity(intent)
//            }
//        )
//    }
//}