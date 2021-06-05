package com.hsn.catetan.view.feature

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hsn.catetan.App
import com.hsn.catetan.data.db.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val dao = App.db.noteDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var noteLD: LiveData<List<NoteEntity>>? = null

    fun getNotes(): LiveData<List<NoteEntity>>? {
        noteLD = dao.fetchAllNotes()
        return noteLD
    }

    fun saveNote(noteEntity: NoteEntity) {
        scope.launch { dao.insertNote(noteEntity) }
    }

    fun deleteNote(noteEntity: NoteEntity) {
        scope.launch { dao.deleteNote(noteEntity) }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}