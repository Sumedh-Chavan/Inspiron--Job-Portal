package com.example.csiapp_2.seeker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
//    val auth = FirebaseAuth.getInstance()
//    val userId = auth.currentUser?.uid ?: ""
//    val firestore = FirebaseFirestore.getInstance()

    //mock
    val userId = "mock_user_123" // Fake user ID for testing


    var isLoading by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf<List<String>>(emptyList()) }

//    // Fetch notifications from Firestore
//    LaunchedEffect(userId) {
//        if (userId.isNotEmpty()) {
//            firestore.collection("users").document(userId).get()
//                .addOnSuccessListener { doc ->
//                    val fetchedNotifications = doc.get("notifications") as? List<String> ?: emptyList()
//                    notifications = fetchedNotifications
//                    isLoading = false
//                }
//                .addOnFailureListener {
//                    isLoading = false
//                }
//        } else {
//            isLoading = false
//        }
//    }

    // Mock data for testing
    val mockNotifications = listOf(
        "Your job application has been received.",
        "Interview scheduled for Software Engineer position.",
        "Reminder: Complete your profile for better job matches."
    )
    notifications = mockNotifications
    isLoading = false


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
                        items(notifications) { message ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
