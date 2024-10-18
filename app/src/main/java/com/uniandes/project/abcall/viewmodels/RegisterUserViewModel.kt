package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.TokenManager
import com.uniandes.project.abcall.repositories.rest.AuthClient

class RegisterUserViewModel (
    private val authClient: AuthClient,
    private val tokenManager: TokenManager
    ) : ViewModel() {

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    fun registerUser(fullName: String, user: String, password: String, checkPassword: String) {


        /*
        authClient.authenticate(username, password) { auth ->
            if (auth != null) {
                tokenManager.saveAuth(auth)
                _token.value = auth.token
            } else {
                _token.value = null
            }
        }

         */

    }
}