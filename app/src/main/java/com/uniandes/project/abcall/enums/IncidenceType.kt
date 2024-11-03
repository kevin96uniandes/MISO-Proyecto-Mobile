package com.uniandes.project.abcall.enums

enum class IncidenceType(val id: Int, val incidence: String) {
    PROBLEM(1, "Petición"),
    QUESTION_REQUEST(2, "Queja/Reclamo"),
    SUGGESTION(3, "Sugerencia");
}