package com.example.csiapp_2.seeker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Data model matching your Firestore document for a job seeker.
data class SeekerProfile(
    val name: String = "",
    val email: String = "",
    val skills: List<String> = emptyList(),
    val experience: Int = 0,
    val location: String = ""
)

class SeekerProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeekerProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerProfileScreen() {
    // Get current user id from FirebaseAuth.
//    val auth = FirebaseAuth.getInstance()
//    val userId = auth.currentUser?.uid ?: ""
//    val firestore = FirebaseFirestore.getInstance()

    // mock
    val userId = ""

    // UI states for profile fields.
    var isLoading by remember { mutableStateOf(true) }
    var updateInProgress by remember { mutableStateOf(false) }
    var updateSuccess by remember { mutableStateOf<Boolean?>(null) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var skillsText by remember { mutableStateOf("") } // Comma-separated skills.
    var experience by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

//    // Fetch the profile document from Firestore once the Composable enters composition.
//    LaunchedEffect(userId) {
//        if (userId.isNotEmpty()) {
//            firestore.collection("users").document(userId).get()
//                .addOnSuccessListener { doc ->
//                    val profile = doc.toObject(SeekerProfile::class.java)
//                    profile?.let {
//                        name = it.name
//                        email = it.email
//                        skillsText = it.skills.joinToString(", ")
//                        experience = it.experience.toString()
//                        location = it.location
//                    }
//                    isLoading = false
//                }
//                .addOnFailureListener {
//                    isLoading = false
//                }
//        } else {
//            isLoading = false
//        }
//    }

    // Mock Data for Testing
    LaunchedEffect(Unit) {
        isLoading = false
        name = "John Doe"
        email = "johndoe@example.com"
        skillsText = "Kotlin, Jetpack Compose, Firebase"
        experience = "3"
        location = "New York"
    }


    // UI layout begins here.
    Surface(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Show loading indicator while fetching data.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Use vertical scrolling in case of smaller devices.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Profile", style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Name field.
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email is read-only.
                OutlinedTextField(
                    value = email,
                    onValueChange = { },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Editable Skills field; user enters comma-separated values.
                OutlinedTextField(
                    value = skillsText,
                    onValueChange = { skillsText = it },
                    label = { Text("Skills (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Editable Experience field.
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Experience (years)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Editable Location field.
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Update Profile button
                Button(
                    onClick = {

                        // for mock purpose to avoid firebase updates
                        if (userId.isEmpty()) {
                            updateSuccess = true // Simulate success in mock mode
                        }
                        else{
//                            updateInProgress = true
//                            // Prepare the updated profile data.
//                            val updatedProfile = mapOf(
//                                "name" to name,
//                                "skills" to skillsText.split(",").map { it.trim() },
//                                "experience" to (experience.toIntOrNull() ?: 0),
//                                "location" to location
//                            )
//                            // Update the Firestore document.
//                            firestore.collection("users").document(userId)
//                                .update(updatedProfile as Map<String, Any>)
//                                .addOnSuccessListener {
//                                    updateSuccess = true
//                                    updateInProgress = false
//                                }
//                                .addOnFailureListener {
//                                    updateSuccess = false
//                                    updateInProgress = false
//                                }
                        }



                    },
                    enabled = !updateInProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (updateInProgress) "Updating..." else "Update Profile")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show status message after an update attempt.
                updateSuccess?.let { success ->
                    Text(
                        text = if (success) "Profile updated successfully!" else "Update failed. Please try again.",
                        color = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
