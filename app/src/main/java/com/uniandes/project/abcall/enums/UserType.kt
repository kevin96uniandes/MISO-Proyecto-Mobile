package com.uniandes.project.abcall.enums

enum class UserType(val userType: String) {
    CLIENT("cliente"),
    USER("usuario");

    companion object {
        fun fromString(userType: String): UserType {
            return entries.first { it.userType == userType }
        }
    }
}