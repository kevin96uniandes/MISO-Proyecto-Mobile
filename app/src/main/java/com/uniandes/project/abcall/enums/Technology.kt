package com.uniandes.project.abcall.enums

enum class Technology(val channel: String) {
    WEB_CALL("Llamada Telefónica"),
    WEB_EMAIL("Correo Electrónico"),
    MOBILE("App Movil");

    companion object {
        fun ordinalFromChannel(channel: String): Int? {
            if (channel == "Todos") return null
            return Technology.entries.find { it.channel == channel }!!.ordinal + 1
        }
    }
}