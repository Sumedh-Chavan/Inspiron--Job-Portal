package com.example.csiapp_2.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.csiapp_2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class RecruiterRegisterActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecruiterRegisterScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RecruiterRegisterScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var company by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showError by remember { mutableStateOf(false) }

        LaunchedEffect(showError) {
            if (showError) {
                kotlinx.coroutines.delay(4000)
                showError = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.jobportalbg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.7f)
                    .background(Color.Black)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Welcome Recruiter",
                            fontSize = 24.sp,
                            color = Color(0xFF6200EE),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(value = name, onValueChange = { name = it }, label = "Name")
                        CustomTextField(value = email, onValueChange = { email = it }, label = "Email")
                        CustomTextField(value = company, onValueChange = { company = it }, label = "Company")
                        CustomTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

                        if (showError) {
                            Text("Please fill all the details!", color = Color.Red)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ElevatedButton(
                            onClick = {
                                if (name.isNotBlank() && email.isNotBlank() && company.isNotBlank() && password.isNotBlank()) {
                                    showError = false
                                    registerRecruiter(name, email, company, password)
                                } else {
                                    showError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Register")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(this@RecruiterRegisterActivity, LoginActivity::class.java)
                                intent.putExtra("role", "recruiter")
                                startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Already have an account? Log in")
                        }
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
            label = { Text(label, color = Color(0xFF6200EE)) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
        )
    }

    private fun registerRecruiter(name: String, email: String, company: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val fcmToken = tokenTask.result
                        val user = hashMapOf(
                            "role" to "recruiter",
                            "name" to name,
                            "email" to email,
                            "company" to company,
                            "notifications" to emptyList<String>(),
                            "job_posts" to emptyList<String>(),
                            "hasUnreadNotifications" to false,
                            "fcmToken" to fcmToken // Store FCM Token
                        )

                        userId?.let {
                            db.collection("users").document(it).set(user).addOnSuccessListener {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.putExtra("role", "recruiter")
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
                Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
