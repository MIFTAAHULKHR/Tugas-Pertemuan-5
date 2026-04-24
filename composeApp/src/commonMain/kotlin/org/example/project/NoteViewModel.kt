package org.example.project

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NoteViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(
        listOf(
            Note("1", "Tugas Mobile", "Mengerjakan tugas navigasi compose."),
            Note("2", "Belanja", "Beli keperluan dapur dan buah-buahan."),
            Note("3", "Olahraga", "Jogging pagi di ITERA jam 6.", isFavorite = true)
        )
    )
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    fun addNote(title: String, content: String) {
        val newNote = Note(
            id = (notes.value.size + 1).toString(),
            title = title,
            content = content
        )
        _notes.update { it + newNote }
    }

    fun updateNote(id: String, title: String, content: String) {
        _notes.update { list ->
            list.map { if (it.id == id) it.copy(title = title, content = content) else it }
        }
    }

    fun toggleFavorite(id: String) {
        _notes.update { list ->
            list.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun getNoteById(id: String): Note? = notes.value.find { it.id == id }
}
