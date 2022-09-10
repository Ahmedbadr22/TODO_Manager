package com.ahmed_badr.to_do_manager.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed_badr.to_do_manager.data.models.User
import com.ahmed_badr.to_do_manager.data.repositories.AuthenticationRepository
import com.ahmed_badr.to_do_manager.utils.response.AuthResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {
    private val authenticationRepository : AuthenticationRepository by lazy {
        AuthenticationRepository()
    }

    // User auth result status
    private val _userAuthResult = MutableLiveData<AuthResult<User>>()
    val userAuthResult : LiveData<AuthResult<User>>
        get() = _userAuthResult

    /**
     * Sign in with google account then update user authentication status
     * @param account: GoogleSignInAccount
     */
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch(Dispatchers.Main) {
            val authResult = authenticationRepository.signInWithGoogle(account)
            _userAuthResult.postValue(authResult.value)
        }
    }
}