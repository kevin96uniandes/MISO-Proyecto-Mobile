package com.uniandes.project.abcall.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    val id: Int,
    val idPersona: Int? = null,
    val idEmpresa: Int? = null,
    val idTipoUsuario: Int,
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    val contrasena: String,
    val fechaCreacion: Date,
    val fechaActualizacion: Date,
    val esActivo: Boolean,
    val persona: Person? = null
){
    constructor() : this(0, 0, 0, 0, "", "", Date(), Date(), true)
}
