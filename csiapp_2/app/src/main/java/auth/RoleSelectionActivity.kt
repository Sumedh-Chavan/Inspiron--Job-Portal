package com.example.csiapp_2.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.csiapp_2.R

class RoleSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoleSelectionScreen { role ->
                val intent = when (role) {
                    "seeker" -> Intent(this, SeekerRegisterActivity::class.java)
                    "recruiter" -> Intent(this, RecruiterRegisterActivity::class.java)
                    else -> null
                }
                intent?.let { startActivity(it) }
            }
        }
    }
}

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        AsyncImage(
            model = R.drawable.jobportalbg,
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Overlay to enhance text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Adjust alpha for darkness
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Droid Jobs",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = "Please select Your Role",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(0.9f),
                modifier = Modifier.padding(bottom = 30.dp)
            )

            RoleButton("Seeker", Color(0xFF4CAF50)) { onRoleSelected("seeker") }
            Spacer(modifier = Modifier.height(20.dp))
            RoleButton("Recruiter", Color(0xFF1976D2)) { onRoleSelected("recruiter") }
        }
    }
}

@Composable
fun RoleButton(role: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable { onClick() }
            .animateContentSize(animationSpec = spring(Spring.DampingRatioMediumBouncy)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = role,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(10.dp)
        )
    }
}

// 🔹 **Preview Function**
@Preview(showBackground = true)
@Composable
fun PreviewRoleSelectionScreen() {
    RoleSelectionScreen(onRoleSelected = {})
}
