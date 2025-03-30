package com.example.csiapp_2.recruiter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RecruiterInboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecruiterInboxScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterInboxScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val recruiterEmail = auth.currentUser?.email ?: ""

    var notifications by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch notifications for the recruiter
    LaunchedEffect(recruiterEmail) {
        firestore.collection("users")
            .whereEqualTo("email", recruiterEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents.first()
                    notifications = doc.get("notifications") as? List<String> ?: emptyList()
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching notifications", exception)
                isLoading = false
            }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Inbox") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (notifications.isEmpty()) {
                Text("No notifications.", modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn {
                    items(notifications) { notification ->
                        NotificationItem(notification)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notification, style = MaterialTheme.typography.bodyMedium)
        }
    }
}