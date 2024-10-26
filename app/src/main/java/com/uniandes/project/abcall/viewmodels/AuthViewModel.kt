package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.models.Auth
import com.uniandes.project.abcall.repositories.rest.AuthClient

class AuthViewModel (
    private val authClient: AuthClient,
    ) : ViewModel() {

    private val _result = MutableLiveData<ApiResult<Auth>>()
    val result: LiveData<ApiResult<Auth>> get() = _result

    fun authenticate(username: String, password: String) {
        authClient.authenticate(username, password) { auth ->
            _result.value = auth
        }
    }
}