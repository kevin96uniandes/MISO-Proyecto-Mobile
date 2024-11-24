package com.uniandes.project.abcall.models

import com.uniandes.project.abcall.enums.UserType

data class Principal (
    val id: Int,
    val idCompany: Int,
    val idPerson: Int?,
    val userType: UserType
)