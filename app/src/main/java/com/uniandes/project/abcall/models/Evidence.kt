package com.uniandes.project.abcall.models

data class Evidence(
    val evidenciaHistoricos: List<Int>,
    val fechaActualizacion: String,
    val fechaCreacion: String,
    val formato: String,
    val id: String,
    val incidenciaId: Int,
    val nombreAdjunto: String,
    val tamano: String
){
    constructor() : this(emptyList(), "", "", "", "", 0, "", "")
}
