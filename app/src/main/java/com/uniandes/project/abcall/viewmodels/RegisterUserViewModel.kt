package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniandes.project.abcall.config.ApiResult

import com.uniandes.project.abcall.repositories.rest.RegisterUserClient


class RegisterUserViewModel (
    private val registerUserClient: RegisterUserClient
    ) : ViewModel() {

        private val _result = MutableLiveData<ApiResult<RegisterUserClient.RegisterResponse>>()
        val result: LiveData<ApiResult<RegisterUserClient.RegisterResponse>> get() = _result

    fun registerUser(registerUser: RegisterUser) {
        registerUserClient.registerUser(registerUser.toUserRegisterRequestBody()) { code ->
            _result.value = code
        }
    }
}

data class RegisterUser(
    val username: String,
    val lastName: String,
    val names: String,
    val password: String,
    val idIdentityType: Int,
    val identificationNumber: Long,
    val confirmPassword: String,
    val phone: String,
    val email: String
)

internal fun RegisterUser.toUserRegisterRequestBody() =
    RegisterUserClient.UserRegisterRequestBody (
        this.username,
        this.lastName,
        this.names,
        this.password,
        this.idIdentityType,
        this.identificationNumber,
        this.confirmPassword,
        this.phone,
        this.email
    )