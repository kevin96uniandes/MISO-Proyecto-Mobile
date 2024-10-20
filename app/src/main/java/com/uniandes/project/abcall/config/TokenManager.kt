package com.uniandes.project.abcall.config

import android.content.Context
import com.google.gson.Gson
import com.uniandes.project.abcall.models.Auth

class TokenManager(context: Context) {

    // Inicializa SharedPreferences
    private val sharedPreferences = context.getSharedPreferences("ABCAllPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_KEY = "authKey"
        private val gson = Gson()
    }

    // Método para guardar el objeto Auth en SharedPreferences
    fun saveAuth(auth: Auth) {
        val preference = gson.toJson(auth) // Convierte el objeto a JSON
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_KEY, preference) // Guarda el JSON
        editor.apply() // Aplica los cambios
    }

    // Método para recuperar el objeto Auth desde SharedPreferences
    fun getAuth(): Auth? {
        val authJson = sharedPreferences.getString(AUTH_KEY, null) ?: return null // Obtiene el JSON
        return gson.fromJson(authJson, Auth::class.java) // Convierte el JSON de vuelta a Auth
    }

    // Método para eliminar el token
    fun deleteToken() {
        val editor = sharedPreferences.edit()
        editor.remove(AUTH_KEY) // Elimina el token
        editor.apply() // Aplica los cambios
    }
}