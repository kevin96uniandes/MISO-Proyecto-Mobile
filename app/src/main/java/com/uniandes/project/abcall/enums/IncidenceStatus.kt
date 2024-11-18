package com.uniandes.project.abcall.enums

enum class IncidenceStatus(val status: String) {
    OPEN("Abierto"),
    DISMISSED("Desestimado"),
    ESCALATED("Escalado"),
    CLOSED_SATISFACTORILY("Cerrado Satisfactoriamente"),
    CLOSED_UNSATISFACTORILY("Cerrado Insatisfactoriamente"),
    REOPENED("Reaperturado");

    companion object{
        fun ordinalFromChannel(status: String): Int? {
            if (status == "Todos") return null
            return (IncidenceStatus.entries.find { it.status == status }!!.ordinal + 1)
        }
    }

}