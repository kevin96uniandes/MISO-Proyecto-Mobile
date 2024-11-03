package com.uniandes.project.abcall.models

import com.google.gson.annotations.SerializedName

data class Evidence(
    val evidenciaHistoricos: List<Int>,
    val fechaActualizacion: String,
    val fechaCreacion: String,
    val formato: String,
    val id: String,
    val incidenciaId: Int,
    @SerializedName("nombre_adjunto") val nombreAdjunto: String,
    val tamano: String
){
    constructor() : this(emptyList(), "", "", "", "", 0, "", "")
}
