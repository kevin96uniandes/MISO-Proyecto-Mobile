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
    val fechaActualizacion: String,
    val canalId: Int,
    val canalNombre: String,
    val usuarioCreadorId: Int,
    val usuarioAsignadoId: Int,
    val personaId: Int,
    val estadoId: Int,
    val tipoId: Int,
    val person: Person? = null,
    val usuarioCreador: User? = null,
    val usuarioAsignado: User? = null,
    @SerializedName ("estado_nombre") val estadoNombre: String
)