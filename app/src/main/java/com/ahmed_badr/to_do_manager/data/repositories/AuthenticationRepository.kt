package com.ahmed_badr.to_do_manager.data.repositories

import androidx.lifecycle.MutableLiveData
import com.ahmed_badr.to_do_manager.data.models.User
import com.ahmed_badr.to_do_manager.utils.response.AuthResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthenticationRepository {
    private val firebaseAuth : FirebaseAuth by lazy {
        Firebase.auth
    }

    /**
     * Sign in user Google account
     * @param account : GoogleSignInAccount
     * @return result : MutableLiveData<AuthResult<User>>
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount) : MutableLiveData<AuthResult<User>> =
        withContext(Dispatchers.IO) {
            val result = MutableLiveData<AuthResult<User>>()
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user!!.uid
                    val name = authResult.user!!.displayName
                    val email = authResult.user!!.email
                    val user = User(uid, name, email)
                    val isNewUser = authResult.additionalUserInfo!!.isNewUser
                    result.value = AuthResult.Success(user, isNewUser=isNewUser)
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message.toString()
                    result.value = AuthResult.Error(errorMessage)
                }.await()

            result
        }
}