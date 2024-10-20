package com.uniandes.project.abcall.viewmodels

import androidx.lifecycle.ViewModel

import com.uniandes.project.abcall.repositories.rest.RegisterUserClient


class RegisterUserViewModel (
    private val registerUserClient: RegisterUserClient
    ) : ViewModel() {


    fun registerUser(firstName: String, lastName: String, user: String, password: String, checkPassword: String) {

        registerUserClient.registerUser(firstName, lastName, user, password, checkPassword) { code ->
            RegisterUserClient.RegisterResponse(code)
        }

    }
}