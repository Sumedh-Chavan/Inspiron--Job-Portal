//package com.example.csiapp_2.recruiter
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.background
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//
///** ✅ Theme Colors */
//private val PurplePrimary = Color(0xFF6A1B9A) // Deep Purple
//private val PurpleLight = Color(0xFFAB47BC)   // Lighter Purple
//private val White = Color.White
//
///** ✅ ViewModel to fetch job statistics */
//class RecruiterDashboardViewModel : ViewModel() {
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//    private val recruiterEmail = auth.currentUser?.email ?: ""
//
//    private val _jobCount = MutableStateFlow(0)
//    val jobCount: StateFlow<Int> = _jobCount
//
//    private val _totalApplicants = MutableStateFlow(0)
//    val totalApplicants: StateFlow<Int> = _totalApplicants
//
//    init {
//        fetchRecruiterStats()
//    }
//
//    /** ✅ Fetch statistics for jobs posted and total applicants */
//    private fun fetchRecruiterStats() {
//        if (recruiterEmail.isBlank()) return
//
//        firestore.collection("job_posts")
//            .whereEqualTo("email", recruiterEmail)
//            .get()
//            .addOnSuccessListener { documents ->
//                val jobCount = documents.size()
//                var totalApplicants = 0
//
//                for (document in documents) {
//                    val applicants = document.get("applicants") as? List<String> ?: emptyList()
//                    totalApplicants += applicants.size
//                }
//
//                _jobCount.value = jobCount
//                _totalApplicants.value = totalApplicants
//            }
//    }
//}
//
///** ✅ Main Activity */
//class RecruiterDashboardActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            RecruiterDashboardScreen()
//        }
//    }
//}
//
///** ✅ Composable Dashboard Screen */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RecruiterDashboardScreen(viewModel: RecruiterDashboardViewModel = viewModel()) {
//    val context = LocalContext.current
//    val jobCount by viewModel.jobCount.collectAsState()
//    val totalApplicants by viewModel.totalApplicants.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Recruiter Dashboard", color = White) },
//                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = PurplePrimary),
//                actions = {
//                    IconButton(onClick = { /* Navigate to profile */ }) {
//                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = White)
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            NavigationBar(containerColor = PurplePrimary) {
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard", tint = White) },
//                    label = { Text("Home", color = White) },
//                    selected = true,
//                    onClick = {}
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Add, contentDescription = "Post Job", tint = White) },
//                    label = { Text("Post Job", color = White) },
//                    selected = false,
//                    onClick = { context.startActivity(Intent(context, PostJobActivity::class.java)) }
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.People, contentDescription = "Applicants", tint = White) },
//                    label = { Text("Applicants", color = White) },
//                    selected = false,
//                    onClick = { context.startActivity(Intent(context, ViewApplicantsActivity::class.java)) }
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Mail, contentDescription = "Inbox", tint = White) },
//                    label = { Text("Inbox", color = White) },
//                    selected = false,
//                    onClick = { context.startActivity(Intent(context, RecruiterInboxActivity::class.java)) }
//                )
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(Brush.verticalGradient(listOf(PurplePrimary, PurpleLight)))
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Job Statistics Card
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp),
//                colors = CardDefaults.cardColors(containerColor = White)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Total Jobs Posted: $jobCount", style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
//                    Text("Total Applicants: $totalApplicants", style = MaterialTheme.typography.titleMedium, color = PurplePrimary)
//                }
//            }
//
//            // Dashboard Grid
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                DashboardCard("Post Job", Icons.Default.Add, PurplePrimary, onClick = {
//                    context.startActivity(Intent(context, PostJobActivity::class.java))
//                })
//                DashboardCard("View Applicants", Icons.Default.People, PurplePrimary, onClick = {
//                    context.startActivity(Intent(context, ViewApplicantsActivity::class.java))
//                })
//            }
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                DashboardCard("Inbox", Icons.Default.Mail, PurplePrimary, onClick = {
//                    context.startActivity(Intent(context, RecruiterInboxActivity::class.java))
//                })
//                DashboardCard("Logout", Icons.Default.ExitToApp, PurplePrimary, onClick = {
//                    FirebaseAuth.getInstance().signOut()
//                    // Redirect to login screen
//                })
//            }
//        }
//    }
//}
//
///** ✅ Reusable Dashboard Card */
//@Composable
//fun DashboardCard(title: String, icon: ImageVector, cardColor: Color, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .size(150.dp)
//            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = White),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(icon, contentDescription = title, modifier = Modifier.size(48.dp), tint = cardColor)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(title, style = MaterialTheme.typography.bodyMedium, color = cardColor)
//        }
//    }
//}

package com.example.csiapp_2.recruiter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.example.csiapp_2.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class RecruiterDashboardActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            RecruiterDashboardScreen(
                onPostJob = { startActivity(Intent(this, PostJobActivity::class.java)) },
                onInbox = { startActivity(Intent(this, RecruiterInboxActivity::class.java)) },
                onApplicants = { startActivity(Intent(this, ViewApplicantsActivity::class.java)) },
                onLogout = {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun RecruiterDashboardScreen(
    onPostJob: () -> Unit,
    onInbox: () -> Unit,
    onApplicants: () -> Unit,
    onLogout: () -> Unit
) {
    var userName by remember { mutableStateOf("Recruiter") }
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        userName = currentUser?.displayName ?: "Recruiter"
    }

    Scaffold(
        bottomBar = { RecruiterBottomNavigation(onInbox, onApplicants) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color.White)))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Welcome, $userName!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))

            RecruiterDashboardCard("Post Job", Icons.Default.Add, onPostJob)
            RecruiterDashboardCard("Inbox", Icons.Default.Mail, onInbox)
            RecruiterDashboardCard("Applicants", Icons.Default.People, onApplicants)
            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onLogout) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Composable
fun RecruiterDashboardCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun RecruiterBottomNavigation(onInbox: () -> Unit, onApplicants: () -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Mail, contentDescription = "Inbox") },
            label = { Text("Inbox") },
            selected = false,
            onClick = onInbox
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.People, contentDescription = "Applicants") },
            label = { Text("Applicants") },
            selected = false,
            onClick = onApplicants
        )
    }
}
