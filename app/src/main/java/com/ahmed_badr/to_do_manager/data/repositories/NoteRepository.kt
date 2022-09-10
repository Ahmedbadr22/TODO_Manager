package com.ahmed_badr.to_do_manager.data.repositories

import androidx.lifecycle.MutableLiveData
import com.ahmed_badr.to_do_manager.data.models.Note
import com.ahmed_badr.to_do_manager.utils.firebase_services.FirebaseCollections.NOTE_DATE_FIELD
import com.ahmed_badr.to_do_manager.utils.firebase_services.FirebaseCollections.NOTE_TIME_FIELD
import com.ahmed_badr.to_do_manager.utils.firebase_services.FirebaseCollections.NOTE_ADDED_BY_UID_FIELD
import com.ahmed_badr.to_do_manager.utils.firebase_services.FirebaseCollections.NOTE_COLLECTION
import com.ahmed_badr.to_do_manager.utils.response.EmptyResult
import com.ahmed_badr.to_do_manager.utils.response.ResultStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NoteRepository {
    private val noteCollection: CollectionReference by lazy {
        Firebase.firestore.collection(NOTE_COLLECTION)
    }

    // current logged in user uid
    private val currentUserUid = Firebase.auth.currentUser!!.uid

    /**
     * Add Note To Firebase Database
     * @since the function take note to add then update its note id with the return document id
     * @param note: Note
     * @return MutableLiveData<ResultStatus<Note>>
     */
    suspend fun addNote(note: Note): MutableLiveData<ResultStatus<Note>> =
        withContext(Dispatchers.IO) {
            val resultStatus = MutableLiveData<ResultStatus<Note>>()
            noteCollection.add(note)
                .addOnSuccessListener { noteDoc ->
                    val id = noteDoc.id
                    note.id = id
                    CoroutineScope(Dispatchers.IO).launch {
                        noteCollection.document(id).set(note)
                    }
                    resultStatus.value = ResultStatus.Success(note)
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message.toString()
                    resultStatus.value = ResultStatus.Error(errorMessage)
                }
                .await()
            resultStatus
        }

    /**
     * List all notes related to the logged in user from firebase database
     * @since the function return order list of note according to data and time of publish
     * @return MutableLiveData<ResultStatus<MutableList<Note>>>
     */
    suspend fun listNotes(): MutableLiveData<ResultStatus<MutableList<Note>>> =
        withContext(Dispatchers.IO) {
            val resultStatus = MutableLiveData<ResultStatus<MutableList<Note>>>()
            // list all notes with user uid
            noteCollection
                .whereEqualTo(NOTE_ADDED_BY_UID_FIELD, currentUserUid)
                .orderBy(NOTE_DATE_FIELD, Query.Direction.DESCENDING)
                .orderBy(NOTE_TIME_FIELD, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val notes = result.toObjects(Note::class.java).toMutableList()
                    resultStatus.value = ResultStatus.Success(notes)
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message.toString()
                    resultStatus.value = ResultStatus.Error(errorMessage)
                }
                .await()
            resultStatus
        }

    /**
     * Delete Note using given note id
     * @param noteId
     * @return MutableLiveData<EmptyResult>
     */
    suspend fun deleteNote(noteId: String): MutableLiveData<EmptyResult> =
        withContext(Dispatchers.IO) {
            val response = MutableLiveData<EmptyResult>()
            noteCollection.document(noteId).delete()
                .addOnSuccessListener {
                    response.value = EmptyResult.Success()
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message.toString()
                    response.value = EmptyResult.Error(errorMessage)
                }
                .await()
            response
        }

    /**
     * Update note data by given updated note object
     * @param note
     * @return MutableLiveData<ResultStatus<Note>>
     */
    suspend fun updateNote(note: Note): MutableLiveData<ResultStatus<Note>> =
        withContext(Dispatchers.IO) {
            val resultStatus = MutableLiveData<ResultStatus<Note>>()
            val noteId = note.id
            if (noteId != null) {
                noteCollection.document(noteId)
                    .set(note)
                    .addOnSuccessListener {
                        resultStatus.value = ResultStatus.Success(note)
                    }
                    .addOnFailureListener { exception ->
                        val errorMessage = exception.message.toString()
                        resultStatus.value = ResultStatus.Error(errorMessage)
                    }.await()
            } else {
                val errorMessage = "Null Note Id"
                resultStatus.value = ResultStatus.Error(errorMessage)
            }
            resultStatus
        }
}