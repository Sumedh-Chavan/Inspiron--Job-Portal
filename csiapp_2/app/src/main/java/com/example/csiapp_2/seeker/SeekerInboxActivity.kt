package com.example.csiapp_2.seeker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    val fetchedNotifications = doc.get("notifications") as? List<*>
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inbox", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6A1B9A)),
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = { SeekerBottomNavigation(currentScreen = "Inbox") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color.White)))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No messages yet!", color = Color.White)
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

@Composable
fun NotificationCard(notification: Map<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notification["company"] ?: "Unknown Company",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Role: ${notification["role"] ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Salary: ${notification["salary"] ?: "Not disclosed"}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SeekerBottomNavigation(currentScreen: String) {
    val context = LocalContext.current
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == "Home",
            onClick = {
                context.startActivity(Intent(context, SeekerDashboardActivity::class.java))
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inbox, contentDescription = "Inbox") },
            label = { Text("Inbox") },
            selected = currentScreen == "Inbox",
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentScreen == "Profile",
            onClick = {}
        )
    }
}