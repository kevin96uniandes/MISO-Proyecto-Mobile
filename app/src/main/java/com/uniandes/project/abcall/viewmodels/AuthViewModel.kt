package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.repositories.rest.AuthClient

class AuthViewModel (private val authClient: AuthClient) : ViewModel() {

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    fun authenticate(username: String, password: String) {
        authClient.authenticate(username, password) { auth ->
            if (auth != null) {
                _token.value = auth.token
            } else {
                _token.value = null
            }
        }
    }

}