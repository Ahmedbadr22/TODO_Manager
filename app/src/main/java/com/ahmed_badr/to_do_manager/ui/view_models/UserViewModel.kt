package com.ahmed_badr.to_do_manager.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed_badr.to_do_manager.data.models.User
import com.ahmed_badr.to_do_manager.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository : UserRepository by lazy {
        UserRepository()
    }


    /**
     * Add User to firebase database
     * @param user: User
     */
    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.Main) {
            userRepository.addNewUser(user)
        }
    }
}