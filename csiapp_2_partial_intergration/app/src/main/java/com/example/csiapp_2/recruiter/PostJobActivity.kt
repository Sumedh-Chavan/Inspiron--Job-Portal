package com.example.csiapp_2.recruiter

import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview

class PostJobActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostJobScreen()
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen() {
    var companyname by remember { mutableStateOf(TextFieldValue()) }
    var jobTitle by remember { mutableStateOf(TextFieldValue()) }
    var jobDescription by remember { mutableStateOf(TextFieldValue()) }
    var salary by remember { mutableStateOf(TextFieldValue()) }
    var location by remember { mutableStateOf(TextFieldValue()) }
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var postedDate by remember { mutableStateOf(TextFieldValue(currentDate)) }
    var deadlineDate by remember { mutableStateOf(TextFieldValue()) }
    var skillsreq by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    val firestore  = FirebaseFirestore.getInstance()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Post a Job") }
            )
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
                onValueChange = { companyname = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = jobDescription,
                onValueChange = { jobDescription = it },
                label = { Text("Job Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = skillsreq,
                onValueChange = { skillsreq = it },
                label = { Text("Skills Required") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salary") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = postedDate,
                onValueChange = { postedDate = it },
                label = { Text("") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deadlineDate,
                onValueChange = { deadlineDate = it },
                label = { Text("Deadline date: (yyyy-MM-dd") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                          val job = hashMapOf<String,Any>(
                                    "title" to jobTitle.text,
                                    "company" to companyname.text,
                                    "description" to jobDescription.text,
                                    "salary" to salary.text,
                                    "location" to location.text,
                                    "posted_on" to Timestamp(Date()),
                                    "deadline" to deadlineDate.text,
                                    "applicants" to 0,
                                    "selected_seekers" to emptyList<String>()
                          )
                          firestore.collection("job_posts")
                              .add(job)
                              .addOnSuccessListener {
                                  Toast.makeText(context,"Job Posted Successfully!",Toast.LENGTH_SHORT).show()
                              }
                              .addOnFailureListener{
                                  Toast.makeText(context , "Failed to post job!",Toast.LENGTH_SHORT)
                              }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post Job")
            }
        }
    }
}
