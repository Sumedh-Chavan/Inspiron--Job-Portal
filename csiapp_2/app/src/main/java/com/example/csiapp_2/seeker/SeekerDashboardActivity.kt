package com.example.csiapp_2.seeker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
//import com.example.csiapp_2.LoginActivity



class SeekerDashboardActivity: ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            SeekerDashboardScreen(
                onSearchJobs = { startActivity(Intent(this, JobListActivity::class.java)) },
                onInbox = { startActivity(Intent(this, SeekerInboxActivity::class.java)) },
                onProfile = { startActivity(Intent(this, SeekerProfileActivity::class.java)) },
                onLogout = {
//                    auth.signOut()
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
                }
            )
        }
    }
}

@Composable
fun SeekerDashboardScreen(
    onSearchJobs: () -> Unit,
    onInbox: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var userName by remember { mutableStateOf("") }

    // Fetch User Name (Optional)
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        userName = currentUser?.displayName ?: "Job Seeker"
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome, $userName!", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onSearchJobs, modifier = Modifier.fillMaxWidth()) {
                Text("Search Jobs")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onInbox, modifier = Modifier.fillMaxWidth()) {
                Text("Inbox")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) {
                Text("Profile")
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}