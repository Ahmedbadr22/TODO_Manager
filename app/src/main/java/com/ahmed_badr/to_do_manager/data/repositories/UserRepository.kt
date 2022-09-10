package com.ahmed_badr.to_do_manager.data.repositories

import androidx.lifecycle.MutableLiveData
import com.ahmed_badr.to_do_manager.data.models.User
import com.ahmed_badr.to_do_manager.utils.firebase_services.FirebaseCollections.USER_COLLECTION
import com.ahmed_badr.to_do_manager.utils.response.ResultStatus
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    private val userCollection : CollectionReference by lazy {
        Firebase.firestore.collection(USER_COLLECTION)
    }

    /**
     * Add new user to firebase database
     * @param user : User
     * @return MutableLiveData<ResultStatus<User>>
     */
    suspend fun addNewUser(user: User) : MutableLiveData<ResultStatus<User>> =
        withContext(Dispatchers.IO) {
            val resultStatus = MutableLiveData<ResultStatus<User>>()
            userCollection.document(user.uid!!).set(user)
                .addOnSuccessListener {
                    resultStatus.value = ResultStatus.Success(user)
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message.toString()
                    resultStatus.value = ResultStatus.Error(message = errorMessage)
                }.await()
            resultStatus
        }
}