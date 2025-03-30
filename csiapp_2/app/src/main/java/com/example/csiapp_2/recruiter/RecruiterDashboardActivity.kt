package com.example.csiapp_2.recruiter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth

class RecruiterDashboardActivity : ComponentActivity() {
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth  = FirebaseAuth.getInstance()
        setContent {
            RecruiterDashboardScreen()
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterDashboardScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Welcome Recruiter") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Button(
                onClick = { val intent = Intent(context , PostJobActivity::class.java)
                    context.startActivity(intent)},
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Post a Job")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { val intent = Intent(context , ViewApplicantsActivity::class.java)
                    context.startActivity(intent) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("View Applicants")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { val intent = Intent(context , RecruiterInboxActivity::class.java)
                    context.startActivity(intent) },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Inbox")
            }
        }
    }
}
