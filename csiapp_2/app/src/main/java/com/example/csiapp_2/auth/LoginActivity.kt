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
import androidx.compose.ui.graphics.Brush
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
import com.example.csiapp_2.MainActivity
import com.example.csiapp_2.R
import com.example.csiapp_2.seeker.SeekerDashboardActivity
import com.example.csiapp_2.recruiter.RecruiterDashboardActivity
import com.example.csiapp_2.ui.theme.Purple40  // Make sure this is defined the same way as RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val role = intent.getStringExtra("role") // Retrieve role from intent
        setContent {
            LoginScreen(role)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen(role: String?) {
        var email by remember { mutableStateOf("") }
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
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)) // Using the same exact gradient colors from RegisterActivity
                        )
                    )
                    .alpha(0.85f)
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
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Login to Your Account",
                            fontSize = 24.sp,
                            color = Color(0xFF2193b0), // Same text color used in RegisterActivity
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(value = email, onValueChange = { email = it }, label = "Email")
                        CustomTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

                        if (showError) {
                            Text("Please enter valid details!", color = Color.Red, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ElevatedButton(
                            onClick = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    showError = false
                                    loginUser(email, password, role)
                                } else {
                                    showError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF2193b0)) // Matching exact button color
                        ) {
                            Text("Login", color = Color.White, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = if (role == "recruiter") {
                                    Intent(this@LoginActivity, RecruiterRegisterActivity::class.java)
                                } else {
                                    Intent(this@LoginActivity, SeekerRegisterActivity::class.java)
                                }
                                startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2193b0)) // Same color as RegisterActivity
                        ) {
                            Text("Don't have an account? Register", fontSize = 16.sp)
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
            label = { Text(label, color = Purple40) }, // Ensure it's the same `Purple40` as in RegisterActivity
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
        )
    }

    private fun loginUser(email: String, password: String, role: String?) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                val intent = when (role) {
                    "seeker" -> Intent(this, SeekerDashboardActivity::class.java)
                    "recruiter" -> Intent(this, RecruiterDashboardActivity::class.java)
                    else -> Intent(this, MainActivity::class.java) // Default fallback
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}