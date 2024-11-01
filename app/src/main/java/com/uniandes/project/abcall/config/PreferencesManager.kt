package com.uniandes.project.abcall.config

import android.content.Context
import com.google.gson.Gson
import com.uniandes.project.abcall.models.Principal

class PreferencesManager(context: Context) {

    val sharedPreferences = context.getSharedPreferences("ABCAllPreferences", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_KEY = "authKey"
        private val gson = Gson()
    }

    // Método para guardar el objeto Auth en SharedPreferences
    fun savePrincipal(principal: Principal) {
        val preference = gson.toJson(principal) // Convierte el objeto a JSON
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_KEY, preference) // Guarda el JSON
        editor.apply() // Aplica los cambios
    }

    fun getAuth(): Principal {
        val authJson = sharedPreferences.getString(AUTH_KEY, null)
        return gson.fromJson(authJson, Principal::class.java)
    }

    // Método para eliminar el token
    fun deletePrincipal() {
        val editor = sharedPreferences.edit()
        editor.remove(AUTH_KEY) // Elimina el token
        editor.apply() // Aplica los cambios
    }
}