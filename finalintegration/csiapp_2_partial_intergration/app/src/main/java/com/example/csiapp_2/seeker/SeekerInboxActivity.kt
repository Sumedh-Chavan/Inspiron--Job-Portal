package com.example.csiapp_2.seeker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.csiapp_2.recruiter.JobDetailsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SeekerInboxActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeekerInboxScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerInboxScreen() {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val firestore = FirebaseFirestore.getInstance()

    var isLoading by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    // Fetch notifications from Firestore
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    val fetchedNotifications = doc.get("notifications") as? List<*>

                    // Safely convert data to List<Map<String, String>>
                    notifications = fetchedNotifications?.mapNotNull { item ->
                        if (item is Map<*, *>) {
                            item.mapKeys { it.key.toString() }.mapValues { it.value.toString() }
                        } else {
                            null
                        }
                    } ?: emptyList()

                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    // UI
    Surface(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Inbox", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No messages yet!")
                    }
                } else {
                    LazyColumn {
                        items(notifications) { notification ->
                            NotificationCard(notification)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NotificationCard(notification: Map<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notification["company"] ?: "Unknown Company",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Role: ${notification["role"] ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Salary: ${notification["salary"] ?: "Not disclosed"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}