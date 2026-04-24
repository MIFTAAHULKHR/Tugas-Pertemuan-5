package org.example.project

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val name: String = "Miftahul Khoiriyah",
    val bio: String = "Hi, I’m Miftahul Khoiriyah, an Informatics Engineering student at Institut Teknologi Sumatera (ITERA) with a strong interest in programming, computer systems, networks, and cybersecurity fundamentals. I continuously develop my technical skills through coursework and projects.",
    val email: String = "miftahul.123140064@student.itera.ac.id",
    val phone: String = "+62xxxxx",
    val location: String = "Indonesia",
    val isDarkMode: Boolean = false,
    val isEditing: Boolean = false
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun setEditing(editing: Boolean) {
        _uiState.update { it.copy(isEditing = editing) }
    }

    fun updateProfile(newName: String, newBio: String) {
        _uiState.update { 
            it.copy(
                name = newName,
                bio = newBio,
                isEditing = false
            )
        }
    }
}
