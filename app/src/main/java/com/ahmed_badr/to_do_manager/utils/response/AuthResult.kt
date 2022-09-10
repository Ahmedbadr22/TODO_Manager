package com.ahmed_badr.to_do_manager.utils.response

sealed class AuthResult<T>(
    val data: T? = null,
    val message : String? = null,
    val isNewUser: Boolean = false
) {
    class Success<T>(data: T, isNewUser: Boolean) : AuthResult<T>(data, isNewUser=isNewUser)
    class Error<T>(message: String) : AuthResult<T>(message = message)
}
