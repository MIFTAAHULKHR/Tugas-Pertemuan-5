package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.Flowers

@Composable
fun App(viewModel: ProfileViewModel = viewModel { ProfileViewModel() }) {
    val uiState by viewModel.uiState.collectAsState()

    // Definisi ColorScheme yang lebih kontras untuk Dark/Light Mode
    val colorScheme = if (uiState.isDarkMode) {
        darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            background = Color(0xFF1C1B1F),
            surface = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF2D2D2D),
            onPrimary = Color.White,
            background = Color.White,
            surface = Color.White,
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFF5F5F5),
            onSurfaceVariant = Color(0xFF49454F)
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(
                uiState = uiState,
                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                onEditClick = { viewModel.setEditing(true) },
                onSaveProfile = { name, bio -> viewModel.updateProfile(name, bio) },
                onCancelEdit = { viewModel.setEditing(false) }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onToggleDarkMode: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onSaveProfile: (String, String) -> Unit,
    onCancelEdit: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(
                name = uiState.name,
                title = "Informatics Engineering Student"
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isEditing) {
                EditProfileForm(
                    initialName = uiState.name,
                    initialBio = uiState.bio,
                    onSave = onSaveProfile,
                    onCancel = onCancelEdit
                )
            } else {
                ProfileCard(bio = uiState.bio) {
                    Spacer(modifier = Modifier.height(24.dp))
                    InfoItem(icon = Icons.Outlined.Email, label = uiState.email)
                    InfoItem(icon = Icons.Outlined.Phone, label = uiState.phone)
                    InfoItem(icon = Icons.Outlined.LocationOn, label = uiState.location)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onEditClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Edit Profile", fontWeight = FontWeight.Medium)
                }
            }
        }

        // Dark Mode Toggle Switch (Diletakkan SETELAH Column agar berada di layer atas/clickable)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (uiState.isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Switch(
                checked = uiState.isDarkMode,
                onCheckedChange = onToggleDarkMode
            )
        }
    }
}

@Composable
fun EditProfileForm(
    initialName: String,
    initialBio: String,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf(initialBio) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { onSave(name, bio) },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save")
            }
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
                .background(MaterialTheme.colorScheme.surfaceVariant),
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
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
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
                color = MaterialTheme.colorScheme.primary
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
