package com.uniandes.project.abcall

enum class IdentificationType(val id: Int, val type: String) {
    DNI(1, "Cédula de ciudanía"),
    FOREIGN_DNI(2, "Cédula de extranjería"),
    NIT(3, "NIT"),
    PASSPORT(4, "Pasaporte");

    fun getIdType(): Int {
        return id
    }

    fun getNameType(): String {
        return type
    }
}


enum class IncidenceType(val id: Int, val type: String) {
    REQUEST(1, "Petición"),
    COMPLAINT(2, "Queja/Reclamo"),
    SUGGESTION(3, "Sugerencia");

    fun getIdType(): Int {
        return id
    }

    fun getNameType(): String {
        return type
    }
}