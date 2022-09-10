package com.ahmed_badr.to_do_manager.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed_badr.to_do_manager.data.models.Note
import com.ahmed_badr.to_do_manager.data.repositories.NoteRepository
import com.ahmed_badr.to_do_manager.utils.response.EmptyResult
import com.ahmed_badr.to_do_manager.utils.response.ResultStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val noteRepository : NoteRepository by lazy {
        NoteRepository()
    }


    // note List result status
    private var _notesResultStatus = MutableLiveData<ResultStatus<MutableList<Note>>>()
    val notesResultStatus : LiveData<ResultStatus<MutableList<Note>>>
        get() = _notesResultStatus

    // updated, added or deleted note result status
    private var _noteResultStatus = MutableLiveData<ResultStatus<Note>>()
    val noteResultStatus : LiveData<ResultStatus<Note>>
        get() = _noteResultStatus

    // note deletion result status
    private var _noteDeleteStatus = MutableLiveData<EmptyResult>()
    val noteDeleteStatus : LiveData<EmptyResult>
        get() = _noteDeleteStatus


    /**
     * List All Notes and update notes result status
     */
    fun listNotes() {
        viewModelScope.launch(Dispatchers.Main) {
            val resultStatus = noteRepository.listNotes()
            _notesResultStatus.postValue(resultStatus.value)
        }
    }

    /**
     * Add Note and update note adding status
     * @param note: Note
     */
    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.Main) {
            val resultStatus = noteRepository.addNote(note)
            _noteResultStatus.postValue(resultStatus.value)
        }
    }

    /**
     * Delete Note and update note deletion status
     * @param noteId : String
     */
    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val response = noteRepository.deleteNote(noteId)
            _noteDeleteStatus.postValue(response.value)
        }
    }

    /**
     * Update note and update note result status
     * @param note: Note
     */
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.Main) {
            val response = noteRepository.updateNote(note)
            _noteResultStatus.postValue(response.value)
        }
    }

    /**
     * Reset note status to clean the status
     */
    fun resetNoteStatus() {
        _noteResultStatus = MutableLiveData<ResultStatus<Note>>()
    }
}