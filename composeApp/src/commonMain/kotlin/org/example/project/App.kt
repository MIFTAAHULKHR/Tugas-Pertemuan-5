package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.Flowers

@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2D2D2D),
            onPrimary = Color.White,
            surface = Color.White,
            onSurface = Color(0xFF333333)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. ProfileHeader (Minimalist)
        ProfileHeader(
            name = "Miftahul Khoiriyah",
            title = "Informatics Engineering Student"
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // 2. ProfileCard (Clean/Minimalist version)
        ProfileCard(
            bio = "Hi, I’m Miftahul Khoiriyah, an Informatics Engineering student at Institut Teknologi Sumatera (ITERA) with a strong interest in programming, computer systems, networks, and cybersecurity fundamentals. I continuously develop my technical skills through coursework and projects."
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3. InfoItem (Reusable)
            InfoItem(icon = Icons.Outlined.Email, label = "miftahul.123140064@student.itera.ac.id")
            InfoItem(icon = Icons.Outlined.Phone, label = "+62xxxxx")
            InfoItem(icon = Icons.Outlined.LocationOn, label = "Indonesia")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Send Message", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileHeader(name: String, title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.Flowers),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProfileCard(bio: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Biography",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bio,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 22.sp,
                textAlign = TextAlign.Start
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        content()
    }
}
