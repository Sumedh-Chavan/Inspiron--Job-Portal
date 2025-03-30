//package com.example.csiapp_2.recruiter
//
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//
//class RecruiterInboxActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            RecruiterInboxScreen()
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RecruiterInboxScreen() {
//    val firestore = FirebaseFirestore.getInstance()
//    val auth = FirebaseAuth.getInstance()
//    val recruiterEmail = auth.currentUser?.email ?: ""
//
//    var notifications by remember { mutableStateOf<List<String>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // Fetch notifications for the recruiter
//    LaunchedEffect(recruiterEmail) {
//        firestore.collection("users")
//            .whereEqualTo("email", recruiterEmail)
//            .get()
//            .addOnSuccessListener { documents ->
//                if (!documents.isEmpty) {
//                    val doc = documents.documents.first()
//                    notifications = doc.get("notifications") as? List<String> ?: emptyList()
//                }
//                isLoading = false
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Firestore", "Error fetching notifications", exception)
//                isLoading = false
//            }
//    }
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Inbox") }) }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//            } else if (notifications.isEmpty()) {
//                Text("No notifications.", modifier = Modifier.padding(8.dp))
//            } else {
//                LazyColumn {
//                    items(notifications) { notification ->
//                        NotificationItem(notification)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun NotificationItem(notification: String) {
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(8.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = notification, style = MaterialTheme.typography.bodyMedium)
//        }
//    }
//}

package com.example.csiapp_2.recruiter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/** ✅ Define theme colors */
private val PurplePrimary = Color(0xFF6A1B9A)
private val PurpleLight = Color(0xFFAB47BC)
private val White = Color.White

/** ✅ Activity to view recruiter inbox */
class RecruiterInboxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecruiterInboxScreen()
        }
    }
}

/** ✅ Composable UI for Recruiter Inbox */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterInboxScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val recruiterEmail = auth.currentUser?.email ?: ""

    var notifications by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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
        topBar = {
            TopAppBar(
                title = { Text("Inbox", color = White) },
//                navigationIcon = {
//                    IconButton(onClick = { /* Handle back action */ }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
//                    }
//                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = PurplePrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PurplePrimary, White)))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = White)
                }
            } else if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications.", color = White)
                }
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

/** ✅ Composable for individual notification card */
@Composable
fun NotificationItem(notification: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notification, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PurplePrimary)
        }
    }
}
