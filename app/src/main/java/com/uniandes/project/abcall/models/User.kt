package com.uniandes.project.abcall.models

import java.util.Date

data class User(
    val id: Int,
    val idPersona: Int? = null,
    val idEmpresa: Int? = null,
    val idTipoUsuario: Int,
    val nombreUsuario: String,
    val contrasena: String,
    val fechaCreacion: Date,
    val fechaActualizacion: Date,
    val esActivo: Boolean,
    val persona: Person? = null
)
