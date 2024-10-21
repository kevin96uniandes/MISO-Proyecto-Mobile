package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.uniandes.project.abcall.repositories.rest.RegisterUserClient


class RegisterUserViewModel (
    private val registerUserClient: RegisterUserClient
    ) : ViewModel() {

        private val _code = MutableLiveData<Int?>()
    val code: LiveData<Int?> get() = _code

    fun registerUser(registerUser: RegisterUser) {
        registerUserClient.registerUser(registerUser.toUserRegisterRequestBody()) { code ->
            _code.value = code
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