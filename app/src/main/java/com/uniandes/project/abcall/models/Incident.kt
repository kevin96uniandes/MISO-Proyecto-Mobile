package com.uniandes.project.abcall.models

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.Date

class Incident (
    val id: Int,
    val codigo: String,
    val descripcion: String,
    val asunto: String,
    @SerializedName ("fecha_creacion") val fechaCreacion: String,
    @SerializedName ("fecha_actualizacion") val fechaActualizacion: String,
    val canalId: Int,
    val canalNombre: String,
    val usuarioCreadorId: Int,
    val usuarioAsignadoId: Int,
    val personaId: Int,
    @SerializedName ("estado_id")val estadoId: Int,
    @SerializedName ("tipo") val tipoId: Int,
    @SerializedName ("estado_nombre") val estadoNombre: String,
    val person: Person? = null,
    val usuarioCreador: User? = null,
    val usuarioAsignado: User? = null
){
    constructor() : this(0, "", "", "", "", "", 0, "", 0, 0, 0, 0, 0, "")
}