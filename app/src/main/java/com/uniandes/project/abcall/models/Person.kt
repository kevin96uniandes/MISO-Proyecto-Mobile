package com.uniandes.project.abcall.models

import java.util.Date

data class Person(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val tipoIdentificacion: String,
    val numeroIdentificacion: String,
    val telefono: String? = null,
    val correoElectronico: String? = null,
    val fechaCreacion: Date,
    val fechaActualizacion: Date
){
    constructor() : this(0, "", "", "", "", "", "", Date(), Date())
}
