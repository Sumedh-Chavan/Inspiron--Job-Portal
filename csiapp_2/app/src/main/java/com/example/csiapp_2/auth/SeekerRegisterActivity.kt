package com.example.csiapp_2.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.csiapp_2.ui.theme.LightPurpleGradient
import com.example.csiapp_2.ui.theme.Purple40
import com.example.csiapp_2.ui.theme.Purple80
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SeekerRegisterActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeekerRegisterScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SeekerRegisterScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var skills by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var experience by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showError by remember { mutableStateOf(false) }

        LaunchedEffect(showError) {
            if (showError) {
                delay(4000)
                showError = false
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = LightPurpleGradient) // Light Purple Gradient
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Seeker Registration",
                        fontSize = 26.sp,
                        color = Purple40,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                    CustomTextField(value = email, onValueChange = { email = it }, label = "Email Address")
                    CustomTextField(value = skills, onValueChange = { skills = it }, label = "Key Skills")
                    CustomTextField(value = location, onValueChange = { location = it }, label = "Location")
                    CustomTextField(value = experience, onValueChange = { experience = it }, label = "Years of Experience")
                    CustomTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

                    if (showError) {
                        Text("Please fill all the fields!", color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && skills.isNotBlank() &&
                                location.isNotBlank() && experience.isNotBlank() && password.isNotBlank()
                            ) {
                                showError = false
                                registerSeeker(name, email, skills, location, experience, password)
                            } else {
                                showError = true
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple40
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register", fontSize = 18.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(this@SeekerRegisterActivity, LoginActivity::class.java)
                            intent.putExtra("role", "seeker")
                            startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Already have an account? Log in", fontSize = 16.sp, color = Purple40)
                    }
                }
            }
        }
    }

    @Composable
    fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Purple40) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple40,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
        )
    }

    private fun registerSeeker(name: String, email: String, skills: String, location: String, experience: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val fcmToken = tokenTask.result
                        val user = hashMapOf(
                            "role" to "seeker",
                            "name" to name,
                            "email" to email,
                            "skills" to skills,
                            "location" to location,
                            "experience" to experience,
                            "isLocked" to false,
                            "notifications" to emptyList<String>(),
                            "applied_jobs" to emptyList<String>(),
                            "hasUnreadNotifications" to false,
                            "fcmToken" to fcmToken // Store FCM Token
                        )

                        userId?.let {
                            db.collection("users").document(it).set(user).addOnSuccessListener {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.putExtra("role", "seeker")
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Firestore Error!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "FCM Token Error!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Invalid Details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}