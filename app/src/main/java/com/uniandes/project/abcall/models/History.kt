package com.uniandes.project.abcall.models

import com.google.gson.annotations.SerializedName

data class History(
    val estadoId: Int,
    @SerializedName("fecha_creacion") val fechaCreacion: String,
    val id: String,
    val incidenciaId: Int,
    val observaciones: String,
    val usuarioAsignadoId: Int,
    val usuarioCreadorId: Int,
    val evidence: List<Evidence>? = null,
    val usuarioCreador: User? = null
){
    constructor() : this(0, "", "", 0, "", 0, 0)
}
