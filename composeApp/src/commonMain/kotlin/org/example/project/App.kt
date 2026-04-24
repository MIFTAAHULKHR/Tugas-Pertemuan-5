package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.resources.painterResource

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.Flowers

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Notes : Screen("notes", "Notes", Icons.Default.Description)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object NoteDetail : Screen("note_detail/{noteId}", "Detail")
    object AddNote : Screen("add_note", "Add Note")
    object EditNote : Screen("edit_note/{noteId}", "Edit Note")
}

@Composable
fun App() {
    val navController = rememberNavController()
    val noteViewModel: NoteViewModel = viewModel { NoteViewModel() }
    val profileViewModel: ProfileViewModel = viewModel { ProfileViewModel() }
    val profileUiState by profileViewModel.uiState.collectAsState()

    val colorScheme = if (profileUiState.isDarkMode) {
        darkColorScheme(primary = Color(0xFFD0BCFF), background = Color(0xFF1C1B1F), surface = Color(0xFF1C1B1F))
    } else {
        lightColorScheme(primary = Color(0xFF2D2D2D), background = Color.White, surface = Color.White)
    }

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                if (currentRoute in listOf(Screen.Notes.route, Screen.Favorites.route, Screen.Profile.route)) {
                    NavigationBar {
                        val items = listOf(Screen.Notes, Screen.Favorites, Screen.Profile)
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                if (navBackStackEntry?.destination?.route == Screen.Notes.route) {
                    FloatingActionButton(onClick = { navController.navigate(Screen.AddNote.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Note")
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Notes.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Notes.route) {
                    NotesScreen(noteViewModel, onNoteClick = { id -> 
                        navController.navigate("note_detail/$id")
                    })
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen(noteViewModel, onNoteClick = { id ->
                        navController.navigate("note_detail/$id")
                    })
                }
                composable(Screen.Profile.route) {
                    ProfileTabScreen(profileViewModel)
                }
                composable(
                    route = Screen.NoteDetail.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")
                    NoteDetailScreen(noteId, noteViewModel, 
                        onBack = { navController.popBackStack() },
                        onEdit = { id -> navController.navigate("edit_note/$id") }
                    )
                }
                composable(Screen.AddNote.route) {
                    AddNoteScreen(noteViewModel, onBack = { navController.popBackStack() })
                }
                composable(
                    route = Screen.EditNote.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")
                    EditNoteScreen(noteId, noteViewModel, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun NotesScreen(viewModel: NoteViewModel, onNoteClick: (String) -> Unit) {
    val notes by viewModel.notes.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Notes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(notes) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNoteClick(note.id) }
                ) {
                    ListItem(
                        headlineContent = { Text(note.title) },
                        supportingContent = { Text(note.content, maxLines = 1) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.toggleFavorite(note.id) }) {
                                Icon(
                                    if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (note.isFavorite) Color.Red else Color.Gray
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: NoteViewModel, onNoteClick: (String) -> Unit) {
    val notes by viewModel.notes.collectAsState()
    val favoriteNotes = notes.filter { it.isFavorite }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Favorites", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (favoriteNotes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorite notes yet")
            }
        } else {
            LazyColumn {
                items(favoriteNotes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNoteClick(note.id) }
                    ) {
                        ListItem(headlineContent = { Text(note.title) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteId: String?, viewModel: NoteViewModel, onBack: () -> Unit, onEdit: (String) -> Unit) {
    val note = noteId?.let { viewModel.getNoteById(it) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    note?.let {
                        IconButton(onClick = { onEdit(it.id) }) { Icon(Icons.Default.Edit, null) }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (note != null) {
                Text(note.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(note.content, style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("Note not found")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(viewModel: NoteViewModel, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), minLines = 5)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.addNote(title, content)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Note")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(noteId: String?, viewModel: NoteViewModel, onBack: () -> Unit) {
    val note = noteId?.let { viewModel.getNoteById(it) }
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), minLines = 5)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    noteId?.let { viewModel.updateNote(it, title, content) }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Note")
            }
        }
    }
}

@Composable
fun ProfileTabScreen(viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onToggleDarkMode = { viewModel.toggleDarkMode(it) },
        onEditClick = { viewModel.setEditing(true) },
        onSaveProfile = { name, bio -> viewModel.updateProfile(name, bio) },
        onCancelEdit = { viewModel.setEditing(false) }
    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(name = uiState.name, title = "Informatics Engineering Student")
            Spacer(modifier = Modifier.height(32.dp))
            if (uiState.isEditing) {
                EditProfileForm(initialName = uiState.name, initialBio = uiState.bio, onSave = onSaveProfile, onCancel = onCancelEdit)
            } else {
                ProfileCard(bio = uiState.bio) {
                    Spacer(modifier = Modifier.height(24.dp))
                    InfoItem(icon = Icons.Outlined.Email, label = uiState.email)
                    InfoItem(icon = Icons.Outlined.Phone, label = uiState.phone)
                    InfoItem(icon = Icons.Outlined.LocationOn, label = uiState.location)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onEditClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp)) {
                    Text("Edit Profile", fontWeight = FontWeight.Medium)
                }
            }
        }
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(20.dp)).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = if (uiState.isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Switch(checked = uiState.isDarkMode, onCheckedChange = onToggleDarkMode)
        }
    }
}

@Composable
fun ProfileHeader(name: String, title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(Res.drawable.Flowers), contentDescription = "Profile Picture", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp))
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ProfileCard(bio: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Biography", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = bio, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, textAlign = TextAlign.Start), color = MaterialTheme.colorScheme.onSurface)
        content()
    }
}

@Composable
fun EditProfileForm(initialName: String, initialBio: String, onSave: (String, String) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf(initialName) }
    var bio by remember { mutableStateOf(initialBio) }
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), minLines = 3)
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f).height(50.dp)) { Text("Cancel") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { onSave(name, bio) }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(8.dp)) { Text("Save") }
        }
    }
}
